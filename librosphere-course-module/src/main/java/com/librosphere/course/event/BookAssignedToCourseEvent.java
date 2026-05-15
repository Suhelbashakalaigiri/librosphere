package com.librosphere.course.event;

import com.librosphere.course.enums.MaterialType;

public record BookAssignedToCourseEvent(
    Long courseMaterialId,
    Long courseId,
    Long bookId,
    Integer bookVersion,
    MaterialType materialType,
    String assignedBy
) {}
