package com.librosphere.review.service;

import com.librosphere.book.dto.BookDto;
import com.librosphere.book.enums.BookStatus;
import com.librosphere.book.service.BookService;
import com.librosphere.review.entity.BookReview;
import com.librosphere.review.enums.ReviewStatus;
import com.librosphere.review.exception.InvalidBookStateTransitionException;
import com.librosphere.review.exception.ReviewNotFoundException;
import com.librosphere.review.mapper.ReviewHistoryMapper;
import com.librosphere.review.mapper.ReviewMapper;
import com.librosphere.review.repository.BookReviewRepository;
import com.librosphere.review.repository.ReviewHistoryRepository;
import com.librosphere.review.validator.ReviewValidator;
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
class ReviewServiceTest {

    @Mock
    private BookReviewRepository reviewRepository;
    @Mock
    private ReviewHistoryRepository historyRepository;
    @Mock
    private BookService bookService;
    @Mock
    private ReviewMapper reviewMapper;
    @Mock
    private ReviewHistoryMapper historyMapper;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private ReviewValidator reviewValidator;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private BookDto draftBook;
    private BookDto reviewBook;

    @BeforeEach
    void setUp() {
        draftBook = new BookDto(1L, "Title", "ISBN", "Desc", 1L, 1, BookStatus.DRAFT, null, null);
        reviewBook = new BookDto(1L, "Title", "ISBN", "Desc", 1L, 1, BookStatus.REVIEW, null, null);
    }

    @Test
    void submitBookForReview_Success() {
        when(bookService.getBookById(1L)).thenReturn(draftBook);
        when(reviewRepository.findByBookId(1L)).thenReturn(Optional.empty());
        when(reviewRepository.save(any(BookReview.class))).thenAnswer(i -> i.getArguments()[0]);

        reviewService.submitBookForReview(1L);

        verify(bookService).updateBookStatus(1L, BookStatus.REVIEW);
        verify(historyRepository).save(any());
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void submitBookForReview_InvalidState_ThrowsException() {
        when(bookService.getBookById(1L)).thenReturn(reviewBook);

        assertThrows(InvalidBookStateTransitionException.class, () -> reviewService.submitBookForReview(1L));
    }

    @Test
    void approveBook_Success() {
        BookReview review = BookReview.builder().id(1L).bookId(1L).status(ReviewStatus.PENDING).build();
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(bookService.getBookById(1L)).thenReturn(reviewBook);

        reviewService.approveBook(1L);

        assertEquals(ReviewStatus.APPROVED, review.getStatus());
        verify(bookService).updateBookStatus(1L, BookStatus.PUBLISHED);
        verify(historyRepository).save(any());
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void rejectBook_Success() {
        BookReview review = BookReview.builder().id(1L).bookId(1L).status(ReviewStatus.PENDING).build();
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        reviewService.rejectBook(1L, "Bad content");

        assertEquals(ReviewStatus.REJECTED, review.getStatus());
        assertEquals("Bad content", review.getFeedback());
        verify(bookService).updateBookStatus(1L, BookStatus.DRAFT);
    }

    @Test
    void requestChanges_Success() {
        BookReview review = BookReview.builder().id(1L).bookId(1L).status(ReviewStatus.PENDING).build();
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        reviewService.requestChanges(1L, "Fix typos");

        assertEquals(ReviewStatus.CHANGES_REQUESTED, review.getStatus());
        assertEquals("Fix typos", review.getFeedback());
        verify(bookService).updateBookStatus(1L, BookStatus.DRAFT);
    }

    @Test
    void getReviewStatus_NotFound_ThrowsException() {
        when(reviewRepository.findByBookId(1L)).thenReturn(Optional.empty());

        assertThrows(ReviewNotFoundException.class, () -> reviewService.getReviewStatus(1L));
    }
}
