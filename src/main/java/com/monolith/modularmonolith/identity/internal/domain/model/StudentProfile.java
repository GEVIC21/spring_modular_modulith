package com.monolith.modularmonolith.identity.internal.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "student_profiles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "studentId", "gradeLevel", "className"})
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, unique = true, length = 50)
    private String studentId;

    @Column(nullable = false)
    private String gradeLevel;

    private String className;

    private String section;

    private String academicYear;

    private LocalDate enrollmentDate;

    private Boolean scholarship;

    private String scholarshipType;

    private String parentName;

    private String parentPhone;

    private String parentEmail;

    private String emergencyContact;

    private String emergencyPhone;

    private String address;

    private String city;

    private String postalCode;

    private String country;

    private String bloodGroup;

    private String allergies;

    private String medicalNotes;

    @ElementCollection
    @CollectionTable(name = "student_extracurricular", joinColumns = @JoinColumn(name = "student_profile_id"))
    @Column(name = "activity")
    @Builder.Default
    private Set<String> extracurricularActivities = new HashSet<>();
}