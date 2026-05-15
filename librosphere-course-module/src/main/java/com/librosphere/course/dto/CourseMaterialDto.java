package com.librosphere.course.dto;

import com.librosphere.book.dto.BookDto;
import com.librosphere.course.enums.MaterialType;
import java.time.LocalDateTime;

public record CourseMaterialDto(
    Long id,
    Long courseId,
    BookDto book,
    Long bookVersionId,
    MaterialType materialType,
    String assignedBy,
    LocalDateTime assignedAt,
    boolean active
) {}
