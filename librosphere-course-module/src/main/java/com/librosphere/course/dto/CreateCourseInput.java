package com.librosphere.course.dto;

import java.time.LocalDateTime;

public record CreateCourseInput(
        String courseCode,
        String courseName,
        String department,
        String semester,
        Integer credits,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
