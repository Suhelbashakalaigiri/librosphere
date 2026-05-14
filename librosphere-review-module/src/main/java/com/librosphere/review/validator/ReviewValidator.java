package com.librosphere.review.validator;

import com.librosphere.review.exception.InvalidReviewStateException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ReviewValidator {

    public void validateFeedback(String feedback, String action) {
        if (!StringUtils.hasText(feedback)) {
            throw new InvalidReviewStateException(action + " requires feedback/reason.");
        }
    }
}
