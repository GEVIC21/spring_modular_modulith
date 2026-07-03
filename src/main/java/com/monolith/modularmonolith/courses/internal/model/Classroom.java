package com.monolith.modularmonolith.courses.internal.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "classrooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String gradeLevel;

    private String section;

    private String academicYear;

    private String roomNumber;

    private Integer capacity;

    @Builder.Default
    private Boolean active = true;

    @Column(name = "homeroom_teacher_id")
    private Long homeroomTeacherId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "classroom_student_ids", joinColumns = @JoinColumn(name = "classroom_id"))
    @Column(name = "student_id")
    @Builder.Default
    private Set<Long> studentIds = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "classroom_course_ids", joinColumns = @JoinColumn(name = "classroom_id"))
    @Column(name = "course_id")
    @Builder.Default
    private Set<Long> courseIds = new HashSet<>();

    @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Schedule> schedules = new HashSet<>();
}