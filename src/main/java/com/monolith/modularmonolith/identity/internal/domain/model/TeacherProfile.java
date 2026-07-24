package com.monolith.modularmonolith.identity.internal.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teacher_profiles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "teacherId", "employeeId", "department"})
public class TeacherProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, unique = true, length = 50)
    private String teacherId;

    @Column(unique = true, length = 50)
    private String employeeId;

    private String department;

    @ElementCollection
    @CollectionTable(name = "teacher_subjects", joinColumns = @JoinColumn(name = "teacher_profile_id"))
    @Column(name = "subject")
    @Builder.Default
    private List<String> subjects = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "teacher_classes", joinColumns = @JoinColumn(name = "teacher_profile_id"))
    @Column(name = "class_assigned")
    @Builder.Default
    private List<String> classesAssigned = new ArrayList<>();

    private String qualification;

    private LocalDate hireDate;

    private String specialization;

    private String officeLocation;

    private String officeHours;
}