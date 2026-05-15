package com.librosphere.course.dto;

import com.librosphere.course.enums.MaterialType;
import jakarta.validation.constraints.NotNull;

public record UpdateCourseMaterialInput(
    @NotNull Long courseMaterialId,
    @NotNull MaterialType materialType,
    @NotNull Long bookVersionId,
    @NotNull String updatedBy
) {}
