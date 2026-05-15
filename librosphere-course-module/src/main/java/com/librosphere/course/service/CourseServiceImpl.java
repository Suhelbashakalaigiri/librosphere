package com.librosphere.course.service;

import com.librosphere.book.dto.BookDto;
import com.librosphere.book.enums.BookStatus;
import com.librosphere.book.mapper.BookMapper;
import com.librosphere.book.service.BookService;
import com.librosphere.course.dto.*;
import com.librosphere.course.entity.*;
import com.librosphere.course.enums.CourseMaterialAction;
import com.librosphere.course.exception.*;
import com.librosphere.course.event.*;
import com.librosphere.course.mapper.*;
import com.librosphere.course.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseMaterialRepository courseMaterialRepository;
    private final CourseMaterialHistoryRepository historyRepository;
    private final BookService bookService;
    private final BookMapper bookMapper;
    private final CourseMapper courseMapper;
    private final CourseMaterialMapper materialMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public CourseDto createCourse(CreateCourseInput input) {
        courseRepository.findByCourseCode(input.courseCode())
                .ifPresent(course -> {
                    throw new RuntimeException("Course already exists with code: " + input.courseCode());
                });
        Course newCourse = courseMapper.toEntity(input);
        Course savedCourse = courseRepository.save(newCourse);
        return courseMapper.toDto(savedCourse);
    }

    @Override
    public CourseDto updateCourse(UpdateCourseInput updateCourseInput) {
         Course course= courseRepository.findByCourseCode(updateCourseInput.courseCode())
                 .orElseThrow(()-> new CourseNotFoundException("Course not found:"+ updateCourseInput.courseCode()));

         course.setCourseName(updateCourseInput.courseName());
         course.setUpdatedAt(LocalDateTime.now());
         course.setCredits(updateCourseInput.credits());
         course.setSemester(updateCourseInput.semester());
         Course savedCourse = courseRepository.save(course);
         return courseMapper.toDto(savedCourse);
    }

    @Override
    public List<CourseDto> getAllCourse() {
        return courseRepository.findAll().stream().map(courseMapper::toDto).toList();
    }

    @Override
    @Transactional
    public CourseMaterialDto assignBookToCourse(AssignBookInput input) {

        courseRepository.findById(input.courseId())
                .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + input.courseId()));


        BookDto book = bookService.getBookById(input.bookId());
        if (book.status() != BookStatus.PUBLISHED) {
            throw new InvalidBookAssignmentException("Only PUBLISHED books can be assigned. Current status: " + book.status());
        }

        boolean versionExists = bookService.getBookVersions(input.bookId()).stream()
                .anyMatch(v -> Objects.equals(v.versionNumber(), input.bookVersion()));
        if (!versionExists) {
            throw new BookVersionMismatchException("Book version not found or does not belong to the book.");
        }


        courseMaterialRepository.findByCourseIdAndBook_IdAndActiveTrue(input.courseId(), input.bookId())
                .ifPresent(m -> {
                    throw new DuplicateCourseMaterialException("Book is already assigned to this course.");
                });



        CourseMaterial material = CourseMaterial.builder()
                .courseId(input.courseId())
                .book(bookMapper.toEntity(bookService.getBookById(input.bookId())))
                .bookVersion(input.bookVersion())
                .materialType(input.materialType())
                .assignedBy(input.assignedBy())
                .build();

        material = courseMaterialRepository.save(material);


        saveHistory(material, CourseMaterialAction.ASSIGNED, input.assignedBy());


        eventPublisher.publishEvent(new BookAssignedToCourseEvent(
                material.getId(),
                material.getCourseId(),
                material.getBook().getId(),
                material.getBookVersion(),
                material.getMaterialType(),
                material.getAssignedBy()
        ));

        return materialMapper.toDto(material);
    }

    @Override
    @Transactional
    public CourseMaterialDto updateCourseMaterial(UpdateCourseMaterialInput input) {
        CourseMaterial material = courseMaterialRepository.findById(input.courseMaterialId())
                .orElseThrow(() -> new RuntimeException("Course material not found"));

        // Validate Book Version consistency if changed
        if (!Objects.equals(material.getBookVersion(), input.bookVersion())) {
            boolean versionExists = bookService.getBookVersions(material.getBook().getId()).stream()
                    .anyMatch(v -> Objects.equals(v.versionNumber(), input.bookVersion()));
            if (!versionExists) {
                throw new BookVersionMismatchException("Book version not found or does not belong to the book.");
            }
            material.setBookVersion(input.bookVersion());
        }

        material.setMaterialType(input.materialType());
        material = courseMaterialRepository.save(material);


        saveHistory(material, CourseMaterialAction.UPDATED, input.updatedBy());


        eventPublisher.publishEvent(new CourseMaterialUpdatedEvent(
                material.getId(),
                material.getCourseId(),
                material.getBook().getId(),
                material.getBookVersion(),
                material.getMaterialType(),
                input.updatedBy()
        ));

        return materialMapper.toDto(material);
    }

    @Override
    @Transactional
    public boolean removeBookFromCourse(Long courseMaterialId, String removedBy) {
        CourseMaterial material = courseMaterialRepository.findById(courseMaterialId)
                .orElseThrow(() -> new RuntimeException("Course material not found"));

        courseMaterialRepository.delete(material);


        saveHistory(material, CourseMaterialAction.REMOVED, removedBy);

        eventPublisher.publishEvent(new CourseMaterialRemovedEvent(
                material.getId(),
                material.getCourseId(),
                material.getBook().getId(),
                removedBy
        ));

        return true;
    }

    @Override
    public List<CourseMaterialDto> getCourseMaterials(Long courseId) {
        return courseMaterialRepository.findByCourseIdAndActiveTrue(courseId).stream()
                .map(materialMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookDto> getBooksByCourse(Long courseId) {
        return courseMaterialRepository.findByCourseIdAndActiveTrue(courseId).stream()
                .map(material -> bookService.getBookById(material.getBook().getId()))
                .collect(Collectors.toList());
    }

    private void saveHistory(CourseMaterial material, CourseMaterialAction action, String performedBy) {
        CourseMaterialHistory history = CourseMaterialHistory.builder()
                .courseMaterialId(material.getId())
                .courseId(material.getCourseId())
                .bookId(material.getBook().getId())
                .bookVersion(material.getBookVersion())
                .materialType(material.getMaterialType())
                .action(action)
                .performedBy(performedBy)
                .build();
        historyRepository.save(history);
    }
}
