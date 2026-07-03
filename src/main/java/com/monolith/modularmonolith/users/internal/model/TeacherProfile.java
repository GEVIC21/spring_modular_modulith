package com.monolith.modularmonolith.users.internal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "teacher_id", unique = true, nullable = false)
    private String teacherId;

    @Column(name = "department")
    private String department;

    @Column(name = "specialization")
    private String specialization;

    @ElementCollection
    @CollectionTable(name = "teacher_subjects", joinColumns = @JoinColumn(name = "teacher_profile_id"))
    @Column(name = "subject")
    private Set<String> subjects = new HashSet<>();

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "qualification")
    private String qualification;

    @Column(name = "phone")
    private String phone;

    @Column(name = "office_location")
    private String officeLocation;

    @Column(name = "bio", length = 2000)
    private String bio;

    @Column(name = "is_tenured")
    private boolean tenured = false;
}