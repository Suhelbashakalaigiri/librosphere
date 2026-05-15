package com.librosphere.lending.service;

import com.librosphere.book.dto.BookDto;
import com.librosphere.book.enums.BookStatus;
import com.librosphere.book.service.BookService;
import com.librosphere.lending.dto.*;
import com.librosphere.lending.entity.BookInventory;
import com.librosphere.lending.entity.BookIssue;
import com.librosphere.lending.enums.IssueStatus;
import com.librosphere.lending.event.*;
import com.librosphere.lending.exception.*;
import com.librosphere.lending.mapper.InventoryMapper;
import com.librosphere.lending.mapper.IssueMapper;
import com.librosphere.lending.repository.BookInventoryRepository;
import com.librosphere.lending.repository.BookIssueRepository;
import com.librosphere.lending.util.LendingUtils;
import com.librosphere.lending.validator.LendingValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LendingServiceImpl implements LendingService {

    private final BookIssueRepository issueRepository;
    private final BookInventoryRepository inventoryRepository;
    private final BookService bookService;
    private final IssueMapper issueMapper;
    private final InventoryMapper inventoryMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final LendingValidator lendingValidator;

    private static final int MAX_BOOKS_PER_USER = 5;
    private static final int ISSUE_DURATION_DAYS = 14;
    private static final int MAX_RENEWALS = 2;
    private static final BigDecimal DAILY_PENALTY_RATE = BigDecimal.valueOf(10);

    @Override
    @Transactional
    public IssueResponse issueBook(IssueBookInput input) {
        // Idempotency check
        Optional<BookIssue> existingIssueByRequest = issueRepository.findByRequestId(input.requestId());
        if (existingIssueByRequest.isPresent()) {
            return IssueResponse.success(issueMapper.toDto(existingIssueByRequest.get()), "Duplicate request handled (Idempotent)");
        }

        // Validate Book
        BookDto book = bookService.getBookById(input.bookId());
        lendingValidator.validateBookForIssue(book);

        // Validate User Limit
        long activeIssues = issueRepository.countByUserIdAndStatus(input.userId(), IssueStatus.ISSUED);
        if (activeIssues >= MAX_BOOKS_PER_USER) {
            throw new IssueLimitExceededException("User has reached the maximum limit of " + MAX_BOOKS_PER_USER + " issued books.");
        }

        // Duplicate Active Issue Check
        issueRepository.findByUserIdAndBookIdAndStatusIn(input.userId(), input.bookId(), List.of(IssueStatus.ISSUED, IssueStatus.OVERDUE))
                .ifPresent(i -> {
                    throw new DuplicateIssueRequestException("User already has an active issue for this book.");
                });

        // Inventory Check & Update (Optimistic Locking)
        BookInventory inventory = inventoryRepository.findByBookId(input.bookId())
                .orElseThrow(() -> new BookUnavailableException("Inventory record not found for book: " + input.bookId()));

        if (inventory.getAvailableCopies() <= 0) {
            throw new BookUnavailableException("No copies available for book: " + book.title());
        }

        inventory.setAvailableCopies(inventory.getAvailableCopies() - 1);
        try {
            inventoryRepository.save(inventory);
        } catch (Exception e) {
            throw new ConcurrentIssueException("Conflict during inventory update. Please retry.");
        }

        // Create Issue
        BookIssue issue = BookIssue.builder()
                .bookId(input.bookId())
                .userId(input.userId())
                .issueDate(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusDays(ISSUE_DURATION_DAYS))
                .status(IssueStatus.ISSUED)
                .requestId(input.requestId())
                .build();

        issue = issueRepository.save(issue);

        eventPublisher.publishEvent(new BookIssuedEvent(this, issue.getId(), issue.getBookId(), issue.getUserId()));

        return IssueResponse.success(issueMapper.toDto(issue), "Book issued successfully");
    }

    @Override
    @Transactional
    public IssueResponse returnBook(Long issueId) {
        BookIssue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new LendingException("Issue record not found"));

        if (issue.getStatus() == IssueStatus.RETURNED) {
            throw new InvalidReturnException("Book already returned.");
        }

        LocalDateTime returnDate = LocalDateTime.now();
        issue.setReturnDate(returnDate);
        issue.setStatus(IssueStatus.RETURNED);

        // Calculate Penalty
        BigDecimal penalty = LendingUtils.calculatePenalty(issue.getDueDate(), returnDate, DAILY_PENALTY_RATE);
        issue.setPenaltyAmount(penalty);

        // Update Inventory
        BookInventory inventory = inventoryRepository.findByBookId(issue.getBookId())
                .orElseThrow(() -> new LendingException("Inventory record not found"));
        inventory.setAvailableCopies(inventory.getAvailableCopies() + 1);
        inventoryRepository.save(inventory);

        issue = issueRepository.save(issue);

        eventPublisher.publishEvent(new BookReturnedEvent(this, issue.getId(), issue.getBookId(), issue.getUserId()));

        return IssueResponse.success(issueMapper.toDto(issue), "Book returned successfully. Penalty: " + issue.getPenaltyAmount());
    }

    @Override
    @Transactional
    public IssueResponse renewBook(Long issueId) {
        BookIssue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new LendingException("Issue record not found"));

        if (issue.getStatus() != IssueStatus.ISSUED && issue.getStatus() != IssueStatus.OVERDUE) {
            throw new LendingException("Cannot renew a book that is not in ISSUED or OVERDUE status.");
        }

        if (issue.getRenewalCount() >= MAX_RENEWALS) {
            throw new RenewalLimitExceededException("Maximum renewal limit of " + MAX_RENEWALS + " reached.");
        }

        issue.setRenewalCount(issue.getRenewalCount() + 1);
        issue.setDueDate(issue.getDueDate().plusDays(ISSUE_DURATION_DAYS));
        issue.setStatus(IssueStatus.ISSUED); // Reset to ISSUED if it was OVERDUE

        issue = issueRepository.save(issue);

        eventPublisher.publishEvent(new BookRenewedEvent(this, issue.getId(), issue.getBookId(), issue.getUserId()));

        return IssueResponse.success(issueMapper.toDto(issue), "Book renewed successfully. New due date: " + issue.getDueDate());
    }

    @Override
    public List<IssueResponse> bulkIssueBooks(BulkIssueInput input) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<IssueResponse>> futures = input.issues().stream()
                    .map(issueInput -> CompletableFuture.supplyAsync(() -> {
                        try {
                            return issueBook(issueInput);
                        } catch (Exception e) {
                            log.error("Error during bulk issue for user {}: {}", issueInput.userId(), e.getMessage());
                            return IssueResponse.failure(e.getMessage());
                        }
                    }, executor))
                    .collect(Collectors.toList());

            return futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<IssueDto> getIssuedBooksByUser(String userId) {
        return issueRepository.findByUserId(userId).stream()
                .map(issueMapper::toDto)
                .toList();
    }

    @Override
    public List<IssueDto> getIssueHistory(Long bookId) {
        return issueRepository.findByBookId(bookId).stream()
                .map(issueMapper::toDto)
                .toList();
    }

    @Override
    public List<IssueDto> getDueDates(String userId) {
        return issueRepository.findByUserIdAndStatus(userId, IssueStatus.ISSUED).stream()
                .map(issueMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public InventoryDto initializeInventory(InitializeInventoryInput input) {
        inventoryRepository.findByBookId(input.bookId())
                .ifPresent(inv -> {
                    throw new LendingException("Inventory already exists for book ID: " + input.bookId());
                });

        // Verify book existence
        bookService.getBookById(input.bookId());

        BookInventory inventory = BookInventory.builder()
                .bookId(input.bookId())
                .totalCopies(input.totalCopies())
                .availableCopies(input.totalCopies())
                .reservedCopies(0)
                .build();

        return inventoryMapper.toDto(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional
    public InventoryDto updateInventory(UpdateInventoryInput input) {
        BookInventory inventory = inventoryRepository.findByBookId(input.bookId())
                .orElseThrow(() -> new LendingException("Inventory not found for book ID: " + input.bookId()));

        int newTotal = inventory.getTotalCopies() + input.changeInCopies();
        int newAvailable = inventory.getAvailableCopies() + input.changeInCopies();

        if (newTotal < 0 || newAvailable < 0) {
            throw new LendingException("Cannot reduce copies below zero or current issued count.");
        }

        inventory.setTotalCopies(newTotal);
        inventory.setAvailableCopies(newAvailable);

        return inventoryMapper.toDto(inventoryRepository.save(inventory));
    }

    @Override
    public InventoryDto getInventoryByBookId(Long bookId) {
        return inventoryRepository.findByBookId(bookId)
                .map(inventoryMapper::toDto)
                .orElseThrow(() -> new LendingException("Inventory not found for book ID: " + bookId));
    }

    @Override
    public List<InventoryDto> getAllInventories() {
        return inventoryRepository.findAll().stream()
                .map(inventoryMapper::toDto)
                .toList();
    }
}
