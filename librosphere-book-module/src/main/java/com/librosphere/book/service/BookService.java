package com.librosphere.book.service;

import com.librosphere.book.dto.*;
import java.util.List;

public interface BookService {
    BookDto createBook(CreateBookInput input);
    BookDto updateBook(UpdateBookInput input);
    boolean deleteBook(Long id);
    BookDto publishBook(Long id);
    BookDto archiveBook(Long id);
    BookDto versionBook(VersionBookInput input);
    
    BookDto getBookById(Long id);
    List<BookDto> searchBooks(BookSearchCriteria criteria);
    List<BookDto> getBooksByFaculty(Long facultyId);
    List<BookVersionDto> getBookVersions(Long bookId);
}
