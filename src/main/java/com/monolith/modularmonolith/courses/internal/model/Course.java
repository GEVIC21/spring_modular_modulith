package com.monolith.modularmonolith.courses.internal.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    private Integer credits;

    private String department;

    private String gradeLevel;

    @Builder.Default
    private Boolean active = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "course_teacher_ids", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "teacher_id")
    @Builder.Default
    private Set<Long> teacherIds = new HashSet<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Schedule> schedules = new HashSet<>();
}