package com.librosphere.lending.dto;

import java.util.List;

public record BulkIssueInput(
    List<IssueBookInput> issues
) {}
