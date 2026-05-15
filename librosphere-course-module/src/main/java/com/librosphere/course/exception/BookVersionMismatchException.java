package com.librosphere.course.exception;

public class BookVersionMismatchException extends RuntimeException {
    public BookVersionMismatchException(String message) {
        super(message);
    }
}
