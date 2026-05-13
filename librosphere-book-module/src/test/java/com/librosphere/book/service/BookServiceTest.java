package com.librosphere.book.service;

import com.librosphere.book.dto.*;
import com.librosphere.book.entity.Book;
import com.librosphere.book.enums.BookStatus;
import com.librosphere.book.exception.DuplicateISBNException;
import com.librosphere.book.exception.InvalidBookStateException;
import com.librosphere.book.exception.VersioningRequiredException;
import com.librosphere.book.mapper.BookMapper;
import com.librosphere.book.mapper.BookVersionMapper;
import com.librosphere.book.repository.BookRepository;
import com.librosphere.book.repository.BookVersionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookVersionRepository versionRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookVersionMapper versionMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book testBook;
    private CreateBookInput createInput;

    @BeforeEach
    void setUp() {
        testBook = Book.builder()
                .id(1L)
                .title("Test Book")
                .isbn("1234567890")
                .facultyId(1L)
                .currentVersion(1)
                .status(BookStatus.DRAFT)
                .build();

        createInput = new CreateBookInput("Test Book", "1234567890", "Description", 1L, "Initial Content");
    }

    @Test
    void createBook_Success() {
        when(bookRepository.existsByIsbn(anyString())).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        when(bookMapper.toDto(any(Book.class))).thenReturn(new BookDto(1L, "Test Book", "1234567890", null, 1L, 1, BookStatus.DRAFT, null, null));

        BookDto result = bookService.createBook(createInput);

        assertNotNull(result);
        assertEquals("1234567890", result.isbn());
        verify(bookRepository).save(any(Book.class));
        verify(versionRepository).save(any());
    }

    @Test
    void createBook_DuplicateIsbn_ThrowsException() {
        when(bookRepository.existsByIsbn(anyString())).thenReturn(true);

        assertThrows(DuplicateISBNException.class, () -> bookService.createBook(createInput));
    }

    @Test
    void publishBook_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        // testBook is in DRAFT, so we need a REVIEW state first or we change transition rules.
        // Actually, my transition rules: DRAFT -> REVIEW. 
        // Let's set it to REVIEW first.
        testBook.setStatus(BookStatus.REVIEW);
        
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        when(bookMapper.toDto(any(Book.class))).thenReturn(new BookDto(1L, "Test Book", "1234567890", null, 1L, 1, BookStatus.PUBLISHED, null, null));

        BookDto result = bookService.publishBook(1L);

        assertNotNull(result);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void publishBook_InvalidTransition_ThrowsException() {
        testBook.setStatus(BookStatus.ARCHIVED);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        assertThrows(IllegalStateException.class, () -> bookService.publishBook(1L));
    }

    @Test
    void updateBook_Published_ThrowsException() {
        testBook.setStatus(BookStatus.PUBLISHED);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        UpdateBookInput updateInput = new UpdateBookInput(1L, "New Title", null);

        assertThrows(VersioningRequiredException.class, () -> bookService.updateBook(updateInput));
    }

    @Test
    void versionBook_Success() {
        testBook.setStatus(BookStatus.PUBLISHED);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        VersionBookInput versionInput = new VersionBookInput(1L, "New Content", "V2 Changes");

        bookService.versionBook(versionInput);

        assertEquals(2, testBook.getCurrentVersion());
        assertEquals(BookStatus.DRAFT, testBook.getStatus());
        verify(versionRepository).save(any());
    }

    @Test
    void versionBook_NotPublished_ThrowsException() {
        testBook.setStatus(BookStatus.DRAFT);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        VersionBookInput versionInput = new VersionBookInput(1L, "New Content", "V2 Changes");

        assertThrows(InvalidBookStateException.class, () -> bookService.versionBook(versionInput));
    }
}
