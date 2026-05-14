package com.librosphere.review.event;

public record ChangesRequestedEvent(Long bookId, String feedback) {}
