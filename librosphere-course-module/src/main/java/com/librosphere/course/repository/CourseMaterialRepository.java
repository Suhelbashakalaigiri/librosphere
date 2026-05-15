package com.librosphere.course.repository;

import com.librosphere.course.entity.CourseMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseMaterialRepository extends JpaRepository<CourseMaterial, Long> {
    List<CourseMaterial> findByCourseIdAndActiveTrue(Long courseId);
    Optional<CourseMaterial> findByCourseIdAndBook_IdAndActiveTrue(Long courseId, Long bookId);
}
