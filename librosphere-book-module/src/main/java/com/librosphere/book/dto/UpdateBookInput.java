package com.librosphere.book.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateBookInput(
    @NotNull(message = "ID is required")
    Long id,
    
    String title,
    String description
) {}
