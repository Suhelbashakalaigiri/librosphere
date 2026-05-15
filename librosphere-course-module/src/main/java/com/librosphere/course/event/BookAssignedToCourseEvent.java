package com.librosphere.course.event;

import com.librosphere.course.enums.MaterialType;

public record BookAssignedToCourseEvent(
    Long courseMaterialId,
    Long courseId,
    Long bookId,
    Long bookVersionId,
    MaterialType materialType,
    String assignedBy
) {}
