package com.librosphere.review.service;

import com.librosphere.book.dto.BookDto;
import com.librosphere.book.enums.BookStatus;
import com.librosphere.book.service.BookService;
import com.librosphere.review.dto.ReviewHistoryResponse;
import com.librosphere.review.dto.ReviewResponse;
import com.librosphere.review.entity.BookReview;
import com.librosphere.review.entity.ReviewHistory;
import com.librosphere.review.enums.ReviewAction;
import com.librosphere.review.enums.ReviewStatus;
import com.librosphere.review.event.BookApprovedEvent;
import com.librosphere.review.event.BookRejectedEvent;
import com.librosphere.review.event.BookSubmittedForReviewEvent;
import com.librosphere.review.event.ChangesRequestedEvent;
import com.librosphere.review.exception.*;
import com.librosphere.review.mapper.ReviewHistoryMapper;
import com.librosphere.review.mapper.ReviewMapper;
import com.librosphere.review.repository.BookReviewRepository;
import com.librosphere.review.repository.ReviewHistoryRepository;
import com.librosphere.review.validator.ReviewValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final BookReviewRepository reviewRepository;
    private final ReviewHistoryRepository historyRepository;
    private final BookService bookService;
    private final ReviewMapper reviewMapper;
    private final ReviewHistoryMapper historyMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final ReviewValidator reviewValidator;

    @Override
    @Transactional
    public ReviewResponse submitBookForReview(Long bookId) {

        BookDto book = bookService.getBookById(bookId);

        if (book.status() != BookStatus.DRAFT) {
            throw new InvalidBookStateTransitionException(
                    "Only DRAFT books can be submitted for review. Current status: "
                            + book.status()
            );
        }

        Optional<BookReview> existingReviewOpt =
                reviewRepository.findByBookId(bookId);


        existingReviewOpt.ifPresent(r -> {
            if (r.getStatus() == ReviewStatus.PENDING) {
                throw new DuplicateReviewException(
                        "Book is already under review."
                );
            }
        });


        BookReview review = existingReviewOpt
                .map(r -> {
                    r.setStatus(ReviewStatus.PENDING);
                    r.setFeedback(null);
                    r.setReviewedAt(null);
                    return r;
                })
                .orElseGet(() -> BookReview.builder()
                        .bookId(bookId)
                        .status(ReviewStatus.PENDING)
                        .build());

        BookReview savedReview = reviewRepository.save(review);

        bookService.updateBookStatus(bookId, BookStatus.REVIEW);

        createHistory(
                bookId,
                ReviewAction.SUBMIT,
                book.status().name(),
                BookStatus.REVIEW.name(),
                "Submitted for review"
        );

        eventPublisher.publishEvent(
                new BookSubmittedForReviewEvent(bookId)
        );

        return reviewMapper.toResponse(savedReview);
    }

    @Override
    @Transactional
    public ReviewResponse approveBook(Long reviewId) {
        BookReview review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + reviewId));

        if (review.getStatus() != ReviewStatus.PENDING) {
            throw new InvalidReviewStateException("Review is not in PENDING state.");
        }

        BookDto book = bookService.getBookById(review.getBookId());
        if (book.status() != BookStatus.REVIEW) {
            throw new BookNotInReviewStateException("Book is not in REVIEW state.");
        }

        review.setStatus(ReviewStatus.APPROVED);
        review.setReviewedAt(LocalDateTime.now());
        reviewRepository.save(review);

        bookService.updateBookStatus(review.getBookId(), BookStatus.PUBLISHED);

        createHistory(review.getBookId(), ReviewAction.APPROVE, BookStatus.REVIEW.name(), BookStatus.PUBLISHED.name(), "Approved by reviewer");
        eventPublisher.publishEvent(new BookApprovedEvent(review.getBookId()));

        return reviewMapper.toResponse(review);
    }

    @Override
    @Transactional
    public ReviewResponse rejectBook(Long reviewId, String reason) {
        BookReview review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + reviewId));

        if (review.getStatus() != ReviewStatus.PENDING) {
            throw new InvalidReviewStateException("Review is not in PENDING state.");
        }

        reviewValidator.validateFeedback(reason, "Rejection");

        review.setStatus(ReviewStatus.REJECTED);
        review.setFeedback(reason);
        review.setReviewedAt(LocalDateTime.now());
        reviewRepository.save(review);

        bookService.updateBookStatus(review.getBookId(), BookStatus.DRAFT);

        createHistory(review.getBookId(), ReviewAction.REJECT, BookStatus.REVIEW.name(), BookStatus.DRAFT.name(), reason);
        eventPublisher.publishEvent(new BookRejectedEvent(review.getBookId(), reason));

        return reviewMapper.toResponse(review);
    }

    @Override
    @Transactional
    public ReviewResponse requestChanges(Long reviewId, String feedback) {
        BookReview review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + reviewId));

        if (review.getStatus() != ReviewStatus.PENDING) {
            throw new InvalidReviewStateException("Review is not in PENDING state.");
        }

        reviewValidator.validateFeedback(feedback, "Request changes");

        review.setStatus(ReviewStatus.CHANGES_REQUESTED);
        review.setFeedback(feedback);
        review.setReviewedAt(LocalDateTime.now());
        reviewRepository.save(review);

        bookService.updateBookStatus(review.getBookId(), BookStatus.DRAFT);

        createHistory(review.getBookId(), ReviewAction.REQUEST_CHANGES, BookStatus.REVIEW.name(), BookStatus.DRAFT.name(), feedback);
        eventPublisher.publishEvent(new ChangesRequestedEvent(review.getBookId(), feedback));

        return reviewMapper.toResponse(review);
    }

    @Override
    public ReviewResponse getReviewStatus(Long bookId) {
        return reviewRepository.findByBookId(bookId)
            .map(reviewMapper::toResponse)
            .orElseThrow(() -> new ReviewNotFoundException("No review found for book id: " + bookId));
    }

    @Override
    public List<ReviewHistoryResponse> getReviewHistory(Long bookId) {
        List<ReviewHistory> history = historyRepository.findByBookIdOrderByPerformedAtDesc(bookId);
        return historyMapper.toResponseList(history);
    }

    private void createHistory(Long bookId, ReviewAction action, String prev, String next, String remarks) {
        ReviewHistory history = ReviewHistory.builder()
            .bookId(bookId)
            .action(action)
            .previousState(prev)
            .newState(next)
            .remarks(remarks)
            .build();
        historyRepository.save(history);
    }
}
