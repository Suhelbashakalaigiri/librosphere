package com.librosphere.lending.exception;

public class RenewalLimitExceededException extends LendingException {
    public RenewalLimitExceededException(String message) { super(message); }
}
