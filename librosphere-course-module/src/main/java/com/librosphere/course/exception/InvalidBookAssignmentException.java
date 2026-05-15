package com.librosphere.course.exception;

public class InvalidBookAssignmentException extends RuntimeException {
    public InvalidBookAssignmentException(String message) {
        super(message);
    }
}
