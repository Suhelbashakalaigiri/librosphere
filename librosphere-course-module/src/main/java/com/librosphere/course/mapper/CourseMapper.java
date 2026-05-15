package com.librosphere.course.mapper;

import com.librosphere.course.dto.CourseDto;
import com.librosphere.course.dto.CreateCourseInput;
import com.librosphere.course.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.WARN)
public interface CourseMapper {
    CourseDto toDto(Course course);
    Course toEntity(CreateCourseInput courseInput);
}
