package com.librosphere.review.exception;

public class InvalidBookStateTransitionException extends RuntimeException {
    public InvalidBookStateTransitionException(String message) {
        super(message);
    }
}
