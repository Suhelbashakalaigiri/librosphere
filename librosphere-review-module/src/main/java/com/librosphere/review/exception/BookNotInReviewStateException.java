package com.librosphere.review.exception;

public class BookNotInReviewStateException extends RuntimeException {
    public BookNotInReviewStateException(String message) {
        super(message);
    }
}
