package com.librosphere.course.entity;

import com.librosphere.course.enums.CourseMaterialAction;
import com.librosphere.course.enums.MaterialType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "course_material_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseMaterialHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long courseMaterialId;

    @Column(nullable = false)
    private Long courseId;

    @Column(nullable = false)
    private Long bookId;

    @Column(nullable = false)
    private Integer bookVersion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaterialType materialType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseMaterialAction action;

    @Column(nullable = false)
    private String performedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime performedAt;

    @PrePersist
    protected void onCreate() {
        performedAt = LocalDateTime.now();
    }
}
