package com.librosphere.course.dto;

import java.time.LocalDateTime;

public record CourseDto(
    Long id,
    String courseCode,
    String courseName,
    String department,
    String semester,
    Integer credits,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
