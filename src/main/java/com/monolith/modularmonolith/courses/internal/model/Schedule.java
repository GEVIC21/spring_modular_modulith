package com.monolith.modularmonolith.courses.internal.model;

import com.monolith.modularmonolith.users.internal.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(
        name = "schedules",
        indexes = {
                @Index(name = "idx_schedule_day_time", columnList = "dayOfWeek, startTime, endTime"),
                @Index(name = "idx_schedule_academic_year", columnList = "academicYear")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    private String room;

    private String academicYear;

    private String semester;

    @Builder.Default
    private Boolean active = true;
}