package com.librosphere.course.repository;

import com.librosphere.course.entity.CourseMaterialHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseMaterialHistoryRepository extends JpaRepository<CourseMaterialHistory, Long> {
    List<CourseMaterialHistory> findByCourseMaterialId(Long courseMaterialId);
}
