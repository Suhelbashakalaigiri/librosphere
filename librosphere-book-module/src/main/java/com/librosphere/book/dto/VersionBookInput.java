package com.librosphere.book.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VersionBookInput(
    @NotNull(message = "Book ID is required")
    Long bookId,
    
    @NotBlank(message = "Content is required")
    String content,
    
    String changeLog
) {}
