package com.librosphere.lending.dto;

import com.librosphere.lending.enums.IssueStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record IssueDto(
    Long id,
    Long bookId,
    String userId,
    LocalDateTime issueDate,
    LocalDateTime dueDate,
    LocalDateTime returnDate,
    IssueStatus status,
    Integer renewalCount,
    BigDecimal penaltyAmount,
    String requestId
) {}
