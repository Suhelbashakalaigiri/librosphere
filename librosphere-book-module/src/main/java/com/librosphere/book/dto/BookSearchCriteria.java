package com.librosphere.book.dto;

import com.librosphere.book.enums.BookStatus;

public record BookSearchCriteria(
    String title,
    String isbn,
    Long facultyId,
    BookStatus status,
    Integer page,
    Integer size
) {}
