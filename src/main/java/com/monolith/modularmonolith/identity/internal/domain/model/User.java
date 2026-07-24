package com.monolith.modularmonolith.identity.internal.domain.model;

import com.monolith.modularmonolith.shared.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(of = {"id", "email", "username", "firstName", "lastName", "profileType"})
public class User extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(length = 20)
    private String phoneNumber;

    @Column(length = 20)
    private String gender;

    private LocalDate dateOfBirth;

    @Column(length = 50)
    private String nationality;

    @Column(length = 10)
    private String language;

    private String avatarFilename;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Builder.Default
    private boolean emailVerified = false;

    private LocalDateTime lastLoginAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProfileType profileType;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private StudentProfile studentProfile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private TeacherProfile teacherProfile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private AdminProfile adminProfile;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    public boolean hasAnyRole(Role... rolesToCheck) {
        for (Role role : rolesToCheck) {
            if (roles.contains(role)) return true;
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

    public void setStudentProfile(StudentProfile profile) {
        this.studentProfile = profile;
        if (profile != null) profile.setUser(this);
    }

    public void setTeacherProfile(TeacherProfile profile) {
        this.teacherProfile = profile;
        if (profile != null) profile.setUser(this);
    }

    public void setAdminProfile(AdminProfile profile) {
        this.adminProfile = profile;
        if (profile != null) profile.setUser(this);
    }
}