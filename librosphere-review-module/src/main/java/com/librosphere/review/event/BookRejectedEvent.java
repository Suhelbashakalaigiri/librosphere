package com.librosphere.review.event;

public record BookRejectedEvent(Long bookId, String reason) {}
