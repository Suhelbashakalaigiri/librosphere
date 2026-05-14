package com.librosphere.review.service;

import com.librosphere.review.dto.ReviewHistoryResponse;
import com.librosphere.review.dto.ReviewResponse;

import java.util.List;

public interface ReviewService {
    ReviewResponse submitBookForReview(Long bookId);
    ReviewResponse approveBook(Long reviewId);
    ReviewResponse rejectBook(Long reviewId, String reason);
    ReviewResponse requestChanges(Long reviewId, String feedback);
    ReviewResponse getReviewStatus(Long bookId);
    List<ReviewHistoryResponse> getReviewHistory(Long bookId);
}
