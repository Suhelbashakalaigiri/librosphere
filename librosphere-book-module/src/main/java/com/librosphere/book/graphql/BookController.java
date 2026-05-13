package com.librosphere.book.graphql;

import com.librosphere.book.dto.*;
import com.librosphere.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import jakarta.validation.Valid;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @QueryMapping
    public BookDto getBookById(@Argument Long id) {
        return bookService.getBookById(id);
    }

    @QueryMapping
    public List<BookDto> searchBooks(@Argument BookSearchCriteria criteria) {
        return bookService.searchBooks(criteria != null ? criteria : new BookSearchCriteria(null, null, null, null, 0, 10));
    }

    @QueryMapping
    public List<BookDto> getBooksByFaculty(@Argument Long facultyId) {
        return bookService.getBooksByFaculty(facultyId);
    }

    @QueryMapping
    public List<BookVersionDto> getBookVersions(@Argument Long bookId) {
        return bookService.getBookVersions(bookId);
    }

    @MutationMapping
    public BookDto createBook(@Argument @Valid CreateBookInput input) {
        return bookService.createBook(input);
    }

    @MutationMapping
    public BookDto updateBook(@Argument @Valid UpdateBookInput input) {
        return bookService.updateBook(input);
    }

    @MutationMapping
    public boolean deleteBook(@Argument Long id) {
        return bookService.deleteBook(id);
    }

    @MutationMapping
    public BookDto publishBook(@Argument Long id) {
        return bookService.publishBook(id);
    }

    @MutationMapping
    public BookDto archiveBook(@Argument Long id) {
        return bookService.archiveBook(id);
    }

    @MutationMapping
    public BookDto versionBook(@Argument @Valid VersionBookInput input) {
        return bookService.versionBook(input);
    }
}
