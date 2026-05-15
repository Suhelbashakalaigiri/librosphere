package com.librosphere.lending.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateInventoryInput(
    @NotNull Long bookId,
    @NotNull Integer changeInCopies
) {}
