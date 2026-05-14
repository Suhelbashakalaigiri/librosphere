package com.librosphere.review.dto;

import com.librosphere.review.enums.ReviewAction;
import java.time.LocalDateTime;

public record ReviewHistoryResponse(
    Long id,
    Long bookId,
    ReviewAction action,
    String previousState,
    String newState,
    String remarks,
    Long performedBy,
    LocalDateTime performedAt
) {}
