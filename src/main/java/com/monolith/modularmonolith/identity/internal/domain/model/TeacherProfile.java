package com.monolith.modularmonolith.identity.internal.domain.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * Profil enseignant associé à un utilisateur.
 */
@Entity
@Table(name = "teacher_profiles")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "teacherId", "employeeId", "department"})
public class TeacherProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "teacher_id", nullable = false, unique = true, length = 20)
    private String teacherId;

    @Column(name = "employee_id", unique = true, length = 20)
    private String employeeId;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "subjects", length = 500)
    private String subjects;

    @Column(name = "classes_assigned", length = 500)
    private String classesAssigned;

    @Column(name = "qualification", length = 255)
    private String qualification;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "specialization", length = 200)
    private String specialization;

    @Column(name = "office_location", length = 100)
    private String officeLocation;

    @Column(name = "office_hours", length = 255)
    private String officeHours;
}