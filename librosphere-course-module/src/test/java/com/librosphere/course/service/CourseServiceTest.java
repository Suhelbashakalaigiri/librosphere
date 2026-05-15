package com.librosphere.course.service;

import com.librosphere.book.dto.BookDto;
import com.librosphere.book.dto.BookVersionDto;
import com.librosphere.book.enums.BookStatus;
import com.librosphere.book.service.BookService;
import com.librosphere.course.dto.AssignBookInput;
import com.librosphere.course.dto.CourseMaterialDto;
import com.librosphere.course.dto.UpdateCourseMaterialInput;
import com.librosphere.course.entity.Course;
import com.librosphere.course.entity.CourseMaterial;
import com.librosphere.course.enums.MaterialType;
import com.librosphere.course.exception.BookVersionMismatchException;
import com.librosphere.course.exception.DuplicateCourseMaterialException;
import com.librosphere.course.exception.InvalidBookAssignmentException;
import com.librosphere.course.mapper.CourseMaterialMapper;
import com.librosphere.course.repository.CourseMaterialHistoryRepository;
import com.librosphere.course.repository.CourseMaterialRepository;
import com.librosphere.course.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private CourseMaterialRepository courseMaterialRepository;
    @Mock
    private CourseMaterialHistoryRepository historyRepository;
    @Mock
    private BookService bookService;
    @Mock
    private CourseMaterialMapper materialMapper;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CourseServiceImpl courseService;

    private Course course;
    private BookDto publishedBook;
    private BookVersionDto bookVersion;

    @BeforeEach
    void setUp() {
        course = Course.builder().id(1L).courseCode("CS101").build();
        publishedBook = new BookDto(100L, "Title", "ISBN", "Desc", 1L, 1, BookStatus.PUBLISHED, null, null);
        bookVersion = new BookVersionDto(200L, 100L, 1, "Content", "Log", null);
    }

    @Test
    void assignBookToCourse_Success() {
        AssignBookInput input = new AssignBookInput(1L, 100L, 200L, MaterialType.MANDATORY, "admin");

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(bookService.getBookById(100L)).thenReturn(publishedBook);
        when(bookService.getBookVersions(100L)).thenReturn(List.of(bookVersion));
        when(courseMaterialRepository.findByCourseIdAndBookIdAndActiveTrue(1L, 100L)).thenReturn(Optional.empty());
        when(courseMaterialRepository.save(any())).thenAnswer(invocation -> {
            CourseMaterial m = invocation.getArgument(0);
            m.setId(1L);
            return m;
        });
        when(materialMapper.toDto(any())).thenAnswer(invocation -> {
            CourseMaterial m = invocation.getArgument(0);
            return new CourseMaterialDto(m.getId(), m.getCourseId(), m.getBookId(), m.getBookVersionId(), m.getMaterialType(), m.getAssignedBy(), null, true);
        });

        CourseMaterialDto result = courseService.assignBookToCourse(input);

        assertNotNull(result);
        assertEquals(100L, result.bookId());
        verify(historyRepository, times(1)).save(any());
        verify(eventPublisher, times(1)).publishEvent(any());
    }

    @Test
    void assignBookToCourse_Fail_NotPublished() {
        AssignBookInput input = new AssignBookInput(1L, 100L, 200L, MaterialType.MANDATORY, "admin");
        BookDto draftBook = new BookDto(100L, "Title", "ISBN", "Desc", 1L, 1, BookStatus.DRAFT, null, null);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(bookService.getBookById(100L)).thenReturn(draftBook);

        assertThrows(InvalidBookAssignmentException.class, () -> courseService.assignBookToCourse(input));
    }

    @Test
    void assignBookToCourse_Fail_VersionMismatch() {
        AssignBookInput input = new AssignBookInput(1L, 100L, 999L, MaterialType.MANDATORY, "admin");

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(bookService.getBookById(100L)).thenReturn(publishedBook);
        when(bookService.getBookVersions(100L)).thenReturn(List.of(bookVersion));

        assertThrows(BookVersionMismatchException.class, () -> courseService.assignBookToCourse(input));
    }

    @Test
    void assignBookToCourse_Fail_Duplicate() {
        AssignBookInput input = new AssignBookInput(1L, 100L, 200L, MaterialType.MANDATORY, "admin");

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(bookService.getBookById(100L)).thenReturn(publishedBook);
        when(bookService.getBookVersions(100L)).thenReturn(List.of(bookVersion));
        when(courseMaterialRepository.findByCourseIdAndBookIdAndActiveTrue(1L, 100L)).thenReturn(Optional.of(new CourseMaterial()));

        assertThrows(DuplicateCourseMaterialException.class, () -> courseService.assignBookToCourse(input));
    }

    @Test
    void updateCourseMaterial_Success() {
        UpdateCourseMaterialInput input = new UpdateCourseMaterialInput(1L, MaterialType.OPTIONAL, 201L, "admin");
        CourseMaterial material = CourseMaterial.builder().id(1L).bookId(100L).bookVersionId(200L).materialType(MaterialType.MANDATORY).build();
        BookVersionDto newVersion = new BookVersionDto(201L, 100L, 2, "New Content", "Updated", null);

        when(courseMaterialRepository.findById(1L)).thenReturn(Optional.of(material));
        when(bookService.getBookVersions(100L)).thenReturn(List.of(newVersion));
        when(courseMaterialRepository.save(any())).thenReturn(material);

        courseService.updateCourseMaterial(input);

        assertEquals(MaterialType.OPTIONAL, material.getMaterialType());
        assertEquals(201L, material.getBookVersionId());
        verify(historyRepository, times(1)).save(any());
        verify(eventPublisher, times(1)).publishEvent(any());
    }

    @Test
    void removeBookFromCourse_Success() {
        CourseMaterial material = CourseMaterial.builder().id(1L).courseId(1L).bookId(100L).build();

        when(courseMaterialRepository.findById(1L)).thenReturn(Optional.of(material));

        courseService.removeBookFromCourse(1L, "admin");

        verify(courseMaterialRepository, times(1)).delete(material);
        verify(historyRepository, times(1)).save(any());
        verify(eventPublisher, times(1)).publishEvent(any());
    }
}
