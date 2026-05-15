package com.librosphere.lending.exception;

public class ConcurrentIssueException extends LendingException {
    public ConcurrentIssueException(String message) { super(message); }
}
