package com.librosphere.lending.entity;

import com.librosphere.lending.enums.IssueStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "book_issues", indexes = {
    @Index(name = "idx_issue_book_id", columnList = "bookId"),
    @Index(name = "idx_issue_user_id", columnList = "userId"),
    @Index(name = "idx_issue_status", columnList = "status"),
    @Index(name = "idx_issue_due_date", columnList = "dueDate")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uc_request_id", columnNames = {"requestId"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long bookId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private LocalDateTime issueDate;

    @Column(nullable = false)
    private LocalDateTime dueDate;

    private LocalDateTime returnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueStatus status;

    @Builder.Default
    @Column(nullable = false)
    private Integer renewalCount = 0;

    @Builder.Default
    @Column(nullable = false)
    private BigDecimal penaltyAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private String requestId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (issueDate == null) issueDate = LocalDateTime.now();
        if (status == null) status = IssueStatus.ISSUED;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
