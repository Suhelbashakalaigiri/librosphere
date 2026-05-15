package com.librosphere.course.mapper;

import com.librosphere.book.mapper.BookMapper;
import com.librosphere.course.dto.CourseMaterialDto;
import com.librosphere.course.entity.CourseMaterial;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {BookMapper.class})
public interface CourseMaterialMapper {
    CourseMaterialDto toDto(CourseMaterial material);
}
