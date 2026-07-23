package com.monolith.modularmonolith.identity.internal.domain.model;

import com.monolith.modularmonolith.shared.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entité JPA principale représentant un utilisateur du système.
 * Hérite de AuditableEntity pour le tracking des dates de création/modification.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(of = {"id", "username", "email", "profileType", "active", "roles"})
public class User extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "avatar_filename", length = 255)
    private String avatarFilename;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "email_verified", nullable = false)
    @Builder.Default
    private boolean emailVerified = false;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "profile_type", nullable = false, length = 20)
    private ProfileType profileType;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private StudentProfile studentProfile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private TeacherProfile teacherProfile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private AdminProfile adminProfile;

    // === Méthodes métier ===

    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        if (firstName != null) {
            return firstName;
        }
        if (lastName != null) {
            return lastName;
        }
        return username;
    }

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    public boolean hasAnyRole(Role... rolesToCheck) {
        for (Role role : rolesToCheck) {
            if (roles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void verifyEmail() {
        this.emailVerified = true;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    // === Setters bidirectionnels ===

    public void setStudentProfile(StudentProfile studentProfile) {
        this.studentProfile = studentProfile;
        if (studentProfile != null) {
            studentProfile.setUser(this);
        }
    }

    public void setTeacherProfile(TeacherProfile teacherProfile) {
        this.teacherProfile = teacherProfile;
        if (teacherProfile != null) {
            teacherProfile.setUser(this);
        }
    }

    public void setAdminProfile(AdminProfile adminProfile) {
        this.adminProfile = adminProfile;
        if (adminProfile != null) {
            adminProfile.setUser(this);
        }
    }
}