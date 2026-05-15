package com.librosphere.lending.dto;

public record IssueResponse(
    IssueDto issue,
    String message,
    boolean success
) {
    public static IssueResponse success(IssueDto issue, String message) {
        return new IssueResponse(issue, message, true);
    }
    public static IssueResponse failure(String message) {
        return new IssueResponse(null, message, false);
    }
}
