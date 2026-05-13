package com.librosphere.book.exception;

public class VersioningRequiredException extends RuntimeException {
    public VersioningRequiredException() {
        super("Published books cannot be modified directly. Use versionBook mutation instead.");
    }
}
