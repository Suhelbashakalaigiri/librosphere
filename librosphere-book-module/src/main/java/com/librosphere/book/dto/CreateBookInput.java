package com.librosphere.book.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateBookInput(
    @NotBlank(message = "Title is required")
    String title,
    
    @NotBlank(message = "ISBN is required")
    String isbn,
    
    String description,
    
    @NotNull(message = "Faculty ID is required")
    Long facultyId,
    
    @NotBlank(message = "Initial content is required")
    String initialContent
) {}
