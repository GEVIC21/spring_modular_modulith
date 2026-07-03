package com.monolith.modularmonolith.grades.internal.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "grades", indexes = {
        @Index(name = "idx_grade_student", columnList = "studentId"),
        @Index(name = "idx_grade_course", columnList = "courseId"),
        @Index(name = "idx_grade_teacher", columnList = "teacherId"),
        @Index(name = "idx_grade_period", columnList = "academicYear,semester")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Grade {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private Long courseId;

    @Column(nullable = false)
    private Long teacherId;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal value;

    @Column(nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal maxValue = BigDecimal.valueOf(20);

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GradeType type;

    @Column(nullable = false)
    private LocalDate gradeDate;

    @Column(length = 10)
    private String semester;

    @Column(nullable = false, length = 10)
    private String academicYear;

    @Column(length = 500)
    private String comment;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}