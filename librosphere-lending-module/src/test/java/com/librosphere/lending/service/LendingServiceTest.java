package com.librosphere.lending.service;

import com.librosphere.book.dto.BookDto;
import com.librosphere.book.enums.BookStatus;
import com.librosphere.book.service.BookService;
import com.librosphere.lending.dto.IssueBookInput;
import com.librosphere.lending.dto.IssueResponse;
import com.librosphere.lending.entity.BookInventory;
import com.librosphere.lending.entity.BookIssue;
import com.librosphere.lending.enums.IssueStatus;
import com.librosphere.lending.mapper.IssueMapper;
import com.librosphere.lending.repository.BookInventoryRepository;
import com.librosphere.lending.repository.BookIssueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LendingServiceTest {

    @Mock
    private BookIssueRepository issueRepository;
    @Mock
    private BookInventoryRepository inventoryRepository;
    @Mock
    private BookService bookService;
    @Mock
    private IssueMapper issueMapper;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private LendingServiceImpl lendingService;

    private BookDto publishedBook;
    private BookInventory inventory;

    @BeforeEach
    void setUp() {
        publishedBook = new BookDto(1L, "Test Book", "123456", "Desc", 1L, 1, BookStatus.PUBLISHED, null, null);
        inventory = BookInventory.builder()
                .bookId(1L)
                .totalCopies(5)
                .availableCopies(5)
                .reservedCopies(0)
                .build();
    }

    @Test
    void issueBook_Success() {
        IssueBookInput input = new IssueBookInput(1L, "user1", "req1");
        
        when(issueRepository.findByRequestId("req1")).thenReturn(Optional.empty());
        when(bookService.getBookById(1L)).thenReturn(publishedBook);
        when(issueRepository.countByUserIdAndStatus("user1", IssueStatus.ISSUED)).thenReturn(0L);
        when(issueRepository.findByUserIdAndBookIdAndStatusIn(any(), any(), any())).thenReturn(Optional.empty());
        when(inventoryRepository.findByBookId(1L)).thenReturn(Optional.of(inventory));
        
        when(issueRepository.save(any(BookIssue.class))).thenAnswer(invocation -> {
            BookIssue issue = invocation.getArgument(0);
            issue.setId(100L);
            return issue;
        });

        IssueResponse response = lendingService.issueBook(input);

        assertTrue(response.success());
        assertEquals(4, inventory.getAvailableCopies());
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    void returnBook_Success() {
        BookIssue issue = BookIssue.builder()
                .id(100L)
                .bookId(1L)
                .userId("user1")
                .status(IssueStatus.ISSUED)
                .dueDate(java.time.LocalDateTime.now().plusDays(1))
                .build();

        when(issueRepository.findById(100L)).thenReturn(Optional.of(issue));
        when(inventoryRepository.findByBookId(1L)).thenReturn(Optional.of(inventory));
        when(issueRepository.save(any())).thenReturn(issue);

        IssueResponse response = lendingService.returnBook(100L);

        assertTrue(response.success());
        assertEquals(IssueStatus.RETURNED, issue.getStatus());
        assertEquals(6, inventory.getAvailableCopies());
        verify(eventPublisher).publishEvent(any());
    }
}
