package com.librosphere.course.event;

public record CourseMaterialRemovedEvent(
    Long courseMaterialId,
    Long courseId,
    Long bookId,
    String removedBy
) {}
