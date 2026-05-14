package com.librosphere.review.repository;

import com.librosphere.review.entity.ReviewHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewHistoryRepository extends JpaRepository<ReviewHistory, Long> {
    List<ReviewHistory> findByBookIdOrderByPerformedAtDesc(Long bookId);
}
