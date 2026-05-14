package com.librosphere.review.dto;

import com.librosphere.review.enums.ReviewStatus;
import java.time.LocalDateTime;

public record ReviewResponse(
    Long id,
    Long bookId,
    Long reviewerId,
    ReviewStatus status,
    String feedback,
    LocalDateTime reviewedAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
