package com.librosphere.lending.repository;

import com.librosphere.lending.entity.BookIssue;
import com.librosphere.lending.enums.IssueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookIssueRepository extends JpaRepository<BookIssue, Long> {
    List<BookIssue> findByUserId(String userId);
    List<BookIssue> findByBookId(Long bookId);
    List<BookIssue> findByUserIdAndStatus(String userId, IssueStatus status);
    Optional<BookIssue> findByRequestId(String requestId);
    Optional<BookIssue> findByUserIdAndBookIdAndStatusIn(String userId, Long bookId, List<IssueStatus> statuses);
    long countByUserIdAndStatus(String userId, IssueStatus status);
}
