package com.monolith.modularmonolith.users.internal.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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
    @JoinColumn(name = "user_id", nullable = false)
    private SchoolUser user;

    @Column(name = "student_id", unique = true, length = 20)
    private String studentId;

    @Column(name = "registration_number", length = 50)
    private String registrationNumber;

    @Column(name = "grade_level", length = 50)
    private String gradeLevel;

    @Column(name = "class_name", length = 50)
    private String className;

    @Column(length = 50)
    private String section;

    @Column(name = "academic_year", length = 20)
    private String academicYear;

    @Column(name = "place_of_birth", length = 100)
    private String placeOfBirth;

    @Column(name = "parent_name", length = 100)
    private String parentName;

    @Column(name = "parent_phone", length = 20)
    private String parentPhone;

    @Column(name = "parent_email")
    private String parentEmail;

    @Column(name = "emergency_contact", length = 100)
    private String emergencyContact;

    @Column(name = "emergency_contact_phone", length = 20)
    private String emergencyContactPhone;

    @Column(length = 255)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(length = 100)
    private String country;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @Column(name = "graduation_date")
    private LocalDate graduationDate;

    @Column(nullable = false)
    @Builder.Default
    private boolean scholarship = false;

    @Column(name = "scholarship_type", length = 100)
    private String scholarshipType;

    private Double gpa;

    @Column(name = "attendance_rate")
    private Integer attendanceRate;

    @Column(name = "blood_group", length = 10)
    private String bloodGroup;

    @Column(length = 500)
    private String allergies;

    @Column(name = "medical_notes", length = 1000)
    private String medicalNotes;

    @ElementCollection
    @CollectionTable(name = "student_activities", joinColumns = @JoinColumn(name = "student_profile_id"))
    @Column(name = "activity")
    @Builder.Default
    private Set<String> extracurricularActivities = new HashSet<>();
}