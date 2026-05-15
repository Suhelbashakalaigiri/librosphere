package com.librosphere.lending.exception;

public class IssueLimitExceededException extends LendingException {
    public IssueLimitExceededException(String message) { super(message); }
}
