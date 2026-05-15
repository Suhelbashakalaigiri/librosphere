package com.librosphere.course.dto;

import java.time.LocalDateTime;

public record UpdateCourseInput(
    Long id,
    String courseCode,
    String courseName,
    String semester,
    Integer credits,
    LocalDateTime updatedAt
) {}
