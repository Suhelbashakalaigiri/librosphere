package com.librosphere.course.dto;

import com.librosphere.course.enums.MaterialType;
import jakarta.validation.constraints.NotNull;

public record AssignBookInput(
    @NotNull Long courseId,
    @NotNull Long bookId,
    @NotNull Integer bookVersion,
    @NotNull MaterialType materialType,
    @NotNull String assignedBy
) {}
