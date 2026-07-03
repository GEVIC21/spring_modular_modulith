package com.monolith.modularmonolith.grades.internal.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "report_cards", indexes = {
        @Index(name = "idx_rc_student", columnList = "studentId"),
        @Index(name = "idx_rc_period", columnList = "academicYear,semester"),
        @Index(name = "idx_rc_classroom", columnList = "classroomId")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReportCard {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false, length = 10)
    private String academicYear;

    @Column(length = 10)
    private String semester;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "report_card_grade_ids",
            joinColumns = @JoinColumn(name = "report_card_id")
    )
    @Column(name = "grade_id")
    @Builder.Default
    private Set<Long> gradeIds = new HashSet<>();

    @Column(precision = 5, scale = 2)
    private BigDecimal overallAverage;

    private Integer rank;

    private Integer totalStudents;

    private Long classroomId;

    @Column(length = 1000)
    private String principalComment;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    private LocalDateTime generatedAt;
}