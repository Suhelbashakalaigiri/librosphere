package com.librosphere.review.graphql;

import com.librosphere.review.dto.ReviewHistoryResponse;
import com.librosphere.review.dto.ReviewResponse;
import com.librosphere.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @QueryMapping
    public ReviewResponse getReviewStatus(@Argument Long bookId) {
        return reviewService.getReviewStatus(bookId);
    }

    @QueryMapping
    public List<ReviewHistoryResponse> getReviewHistory(@Argument Long bookId) {
        return reviewService.getReviewHistory(bookId);
    }

    @MutationMapping
    public ReviewResponse submitBookForReview(@Argument Long bookId) {
        return reviewService.submitBookForReview(bookId);
    }

    @MutationMapping
    public ReviewResponse approveBook(@Argument Long reviewId) {
        return reviewService.approveBook(reviewId);
    }

    @MutationMapping
    public ReviewResponse rejectBook(@Argument Long reviewId, @Argument String reason) {
        return reviewService.rejectBook(reviewId, reason);
    }

    @MutationMapping
    public ReviewResponse requestChanges(@Argument Long reviewId, @Argument String feedback) {
        return reviewService.requestChanges(reviewId, feedback);
    }
}
