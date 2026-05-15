package com.librosphere.lending.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record InitializeInventoryInput(
    @NotNull Long bookId,
    @NotNull @Min(0) Integer totalCopies
) {}
