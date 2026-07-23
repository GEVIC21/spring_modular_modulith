package com.monolith.modularmonolith.identity.internal.domain.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * Profil administrateur associé à un utilisateur.
 */
@Entity
@Table(name = "admin_profiles")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "adminId", "department", "accessLevel"})
public class AdminProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "admin_id", nullable = false, unique = true, length = 20)
    private String adminId;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "access_level", length = 20)
    private String accessLevel;

    @Column(name = "permissions", length = 1000)
    private String permissions;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "job_title", length = 100)
    private String jobTitle;

    @Column(name = "office_location", length = 100)
    private String officeLocation;
}