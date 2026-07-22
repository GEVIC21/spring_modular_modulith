package com.monolith.modularmonolith.users.internal.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "teacher_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private SchoolUser user;

    @Column(name = "teacher_id", unique = true, length = 20)
    private String teacherId;

    @Column(name = "employee_id", unique = true, length = 20)
    private String employeeId;

    @Column(length = 100)
    private String department;

    @Column(length = 100)
    private String specialization;

    @ElementCollection
    @CollectionTable(name = "teacher_subjects", joinColumns = @JoinColumn(name = "teacher_profile_id"))
    @Column(name = "subject")
    @Builder.Default
    private Set<String> subjects = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "teacher_classes", joinColumns = @JoinColumn(name = "teacher_profile_id"))
    @Column(name = "class_name")
    @Builder.Default
    private Set<String> classesAssigned = new HashSet<>();

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "contract_end_date")
    private LocalDate contractEndDate;

    @Column(name = "contract_type", length = 20)
    private String contractType; // CDI, CDD, VACATAIRE

    @Column(length = 100)
    private String qualification;

    @Column(length = 500)
    private String certifications;

    @Column(length = 20)
    private String phone;

    @Column(name = "office_location", length = 100)
    private String officeLocation;

    @Column(name = "office_hours", length = 100)
    private String officeHours;

    @Column(length = 1000)
    private String bio;

    @Column(name = "research_interests", length = 500)
    private String researchInterests;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Column(nullable = false)
    @Builder.Default
    private boolean tenured = false;

    @Column(name = "department_head", nullable = false)
    @Builder.Default
    private boolean departmentHead = false;
}