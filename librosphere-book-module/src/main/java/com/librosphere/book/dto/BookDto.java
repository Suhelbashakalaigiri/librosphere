package com.librosphere.book.dto;

import com.librosphere.book.enums.BookStatus;
import java.time.LocalDateTime;

public record BookDto(
    Long id,
    String title,
    String isbn,
    String description,
    Long facultyId,
    Integer currentVersion,
    BookStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
