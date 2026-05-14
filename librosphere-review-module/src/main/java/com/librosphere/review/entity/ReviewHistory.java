package com.librosphere.review.entity;

import com.librosphere.review.enums.ReviewAction;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ReviewHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long bookId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewAction action;

    @Column(nullable = false)
    private String previousState;

    @Column(nullable = false)
    private String newState;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    private Long performedBy;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime performedAt;
}
