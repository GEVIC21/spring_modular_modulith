package com.monolith.modularmonolith.users.internal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "student_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "student_id", unique = true, nullable = false)
    private String studentId;

    @Column(name = "registration_number", unique = true)
    private String registrationNumber;

    @Column(name = "grade_level")
    private String gradeLevel;

    @Column(name = "class_name")
    private String className;

    @Column(name = "section")
    private String section;

    @Column(name = "academic_year")
    private String academicYear;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "parent_name")
    private String parentName;

    @Column(name = "parent_phone")
    private String parentPhone;

    @Column(name = "parent_email")
    private String parentEmail;

    @Column(name = "emergency_contact")
    private String emergencyContact;

    @Column(name = "address")
    private String address;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @Column(name = "is_scholarship")
    private boolean scholarship = false;
}