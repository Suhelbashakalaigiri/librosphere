package com.librosphere.lending.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IssueBookInput(
    @NotNull Long bookId,
    @NotBlank String userId,
    @NotBlank String requestId
) {}
