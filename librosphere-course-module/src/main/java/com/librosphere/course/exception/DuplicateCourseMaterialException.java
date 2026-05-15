package com.librosphere.course.exception;

public class DuplicateCourseMaterialException extends RuntimeException {
    public DuplicateCourseMaterialException(String message) {
        super(message);
    }
}
