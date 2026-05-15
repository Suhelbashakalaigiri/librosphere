package com.librosphere.course.entity;

import com.librosphere.book.entity.Book;
import com.librosphere.course.enums.MaterialType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.time.LocalDateTime;

@Entity
@Table(name = "course_materials", indexes = {
    @Index(name = "idx_course_id", columnList = "courseId"),
    @Index(name = "idx_book_version", columnList = "bookVersion")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE course_materials SET active = false WHERE id = ?")
@SQLRestriction("active = true")
public class CourseMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long courseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private Integer bookVersion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaterialType materialType;

    @Column(nullable = false)
    private String assignedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime assignedAt;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    @PrePersist
    protected void onCreate() {
        assignedAt = LocalDateTime.now();
    }
}
