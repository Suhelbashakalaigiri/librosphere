package com.librosphere.lending.dto;

import java.time.LocalDateTime;

public record InventoryDto(
    Long id,
    Long bookId,
    Integer totalCopies,
    Integer availableCopies,
    Integer reservedCopies,
    LocalDateTime updatedAt
) {}
