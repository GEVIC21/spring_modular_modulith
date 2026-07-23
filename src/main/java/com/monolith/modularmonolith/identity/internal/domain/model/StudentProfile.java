package com.monolith.modularmonolith.identity.internal.domain.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * Profil étudiant associé à un utilisateur.
 */
@Entity
@Table(name = "student_profiles")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "studentId", "gradeLevel", "className"})
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "student_id", nullable = false, unique = true, length = 20)
    private String studentId;

    @Column(name = "grade_level", length = 20)
    private String gradeLevel;

    @Column(name = "class_name", length = 50)
    private String className;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @Column(name = "parent_name", length = 150)
    private String parentName;

    @Column(name = "parent_phone", length = 20)
    private String parentPhone;

    @Column(name = "parent_email", length = 255)
    private String parentEmail;

    @Column(name = "medical_info", length = 1000)
    private String medicalInfo;

    @Column(name = "emergency_contact", length = 150)
    private String emergencyContact;

    @Column(name = "emergency_phone", length = 20)
    private String emergencyPhone;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
}