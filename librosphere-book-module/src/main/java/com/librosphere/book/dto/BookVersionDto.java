package com.librosphere.book.dto;

import java.time.LocalDateTime;

public record BookVersionDto(
    Long id,
    Long bookId,
    Integer versionNumber,
    String content,
    String changeLog,
    LocalDateTime createdAt
) {}
