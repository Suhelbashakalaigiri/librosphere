package com.librosphere.course.graphql;

import com.librosphere.book.dto.BookDto;
import com.librosphere.course.dto.*;
import com.librosphere.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @MutationMapping
    public CourseDto createCourse(@Argument CreateCourseInput input){
        return courseService.createCourse(input);
    }
    @MutationMapping
    public CourseDto updateCourse(@Argument UpdateCourseInput updateCourseInput){
        return courseService.updateCourse(updateCourseInput);
    }
    @MutationMapping
    public CourseMaterialDto assignBookToCourse(@Argument AssignBookInput input) {
        return courseService.assignBookToCourse(input);
    }

    @MutationMapping
    public CourseMaterialDto updateCourseMaterial(@Argument UpdateCourseMaterialInput input) {
        return courseService.updateCourseMaterial(input);
    }

    @MutationMapping
    public boolean removeBookFromCourse(@Argument Long courseMaterialId, @Argument String removedBy) {
        return courseService.removeBookFromCourse(courseMaterialId, removedBy);
    }

    @QueryMapping
    public List<CourseDto> getAllCourses(){
        return courseService.getAllCourse();
    }

    @QueryMapping
    public List<CourseMaterialDto> getCourseMaterials(@Argument Long courseId) {
        return courseService.getCourseMaterials(courseId);
    }

    @QueryMapping
    public List<BookDto> getBooksByCourse(@Argument Long courseId) {
        return courseService.getBooksByCourse(courseId);
    }
}
