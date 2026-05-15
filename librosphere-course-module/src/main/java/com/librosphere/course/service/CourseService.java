package com.librosphere.course.service;

import com.librosphere.course.dto.*;
import com.librosphere.course.entity.Course;

import java.util.List;

public interface CourseService {
    CourseDto createCourse(CreateCourseInput input);
    CourseDto updateCourse(UpdateCourseInput updateCourseInput);
    List<CourseDto> getAllCourse();
    CourseMaterialDto assignBookToCourse(AssignBookInput input);
    CourseMaterialDto updateCourseMaterial(UpdateCourseMaterialInput input);
    boolean removeBookFromCourse(Long courseMaterialId, String removedBy);
    List<CourseMaterialDto> getCourseMaterials(Long courseId);
    List<com.librosphere.book.dto.BookDto> getBooksByCourse(Long courseId);
}
