package com.monolith.modularmonolith.users.internal.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "admin_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private SchoolUser user;

    @Column(name = "admin_id", unique = true, length = 20)
    private String adminId;

    @Column(length = 100)
    private String department;

    @Column(name = "job_title", length = 100)
    private String jobTitle;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "access_level", length = 20)
    private String accessLevel; // FULL, LIMITED, READ_ONLY

    @ElementCollection
    @CollectionTable(name = "admin_modules", joinColumns = @JoinColumn(name = "admin_profile_id"))
    @Column(name = "module")
    @Builder.Default
    private Set<String> managedModules = new HashSet<>();

    @Column(name = "can_manage_users", nullable = false)
    @Builder.Default
    private boolean canManageUsers = false;

    @Column(name = "can_manage_finances", nullable = false)
    @Builder.Default
    private boolean canManageFinances = false;

    @Column(name = "can_manage_academics", nullable = false)
    @Builder.Default
    private boolean canManageAcademics = false;

    @Column(name = "office_phone", length = 20)
    private String officePhone;

    @Column(name = "office_location", length = 100)
    private String officeLocation;
}