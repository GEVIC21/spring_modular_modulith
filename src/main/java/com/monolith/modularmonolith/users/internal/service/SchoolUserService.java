package com.monolith.modularmonolith.users.internal.service;

import com.monolith.modularmonolith.users.internal.dto.request.RoleAssignmentRequest;
import com.monolith.modularmonolith.users.internal.dto.request.StudentRegisterRequest;
import com.monolith.modularmonolith.users.internal.dto.request.TeacherRegisterRequest;
import com.monolith.modularmonolith.users.internal.dto.response.StudentProfileResponse;
import com.monolith.modularmonolith.users.internal.dto.response.TeacherProfileResponse;
import com.monolith.modularmonolith.users.internal.dto.response.UserListResponse;
import com.monolith.modularmonolith.users.internal.model.*;
import com.monolith.modularmonolith.users.internal.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.monolith.modularmonolith.users.internal.model.SchoolConstants.*;

@Service
@RequiredArgsConstructor
public class SchoolUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AvatarService avatarService;


    // ==================== INSCRIPTION ÉLÈVE ====================

    @Transactional
    public StudentProfileResponse registerStudent(StudentRegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé.");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Ce nom d'utilisateur est déjà pris.");
        }
        if (studentProfileRepository.existsByStudentId(request.studentId())) {
            throw new IllegalArgumentException("Cet ID étudiant existe déjà.");
        }

        Role studentRole = roleRepository.findByName(ROLE_ELEVE)
                .orElseThrow(() -> new IllegalStateException("Rôle ELEVE non configuré"));

        User user = new User(
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password()),
                new HashSet<>(Collections.singletonList(studentRole))
        );
        user = userRepository.save(user);

        StudentProfile profile = StudentProfile.builder()
                .user(user)
                .studentId(request.studentId())
                .registrationNumber(request.registrationNumber())
                .gradeLevel(request.gradeLevel())
                .className(request.className())
                .section(request.section())
                .academicYear(request.academicYear())
                .birthDate(request.birthDate())
                .parentName(request.parentName())
                .parentPhone(request.parentPhone())
                .parentEmail(request.parentEmail())
                .emergencyContact(request.emergencyContact())
                .address(request.address())
                .enrollmentDate(request.enrollmentDate() != null ? request.enrollmentDate() : java.time.LocalDate.now())
                .scholarship(request.scholarship())
                .build();

        studentProfileRepository.save(profile);

        return mapToStudentResponse(user, profile);
    }

    // ==================== INSCRIPTION ENSEIGNANT ====================

    @Transactional
    public TeacherProfileResponse registerTeacher(TeacherRegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé.");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Ce nom d'utilisateur est déjà pris.");
        }
        if (teacherProfileRepository.existsByTeacherId(request.teacherId())) {
            throw new IllegalArgumentException("Cet ID enseignant existe déjà.");
        }

        Role teacherRole = roleRepository.findByName(ROLE_ENSEIGNANT)
                .orElseThrow(() -> new IllegalStateException("Rôle ENSEIGNANT non configuré"));

        User user = new User(
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password()),
                new HashSet<>(Collections.singletonList(teacherRole))
        );
        user = userRepository.save(user);

        TeacherProfile profile = TeacherProfile.builder()
                .user(user)
                .teacherId(request.teacherId())
                .department(request.department())
                .specialization(request.specialization())
                .subjects(request.subjects() != null ? request.subjects() : new HashSet<>())
                .hireDate(request.hireDate())
                .qualification(request.qualification())
                .phone(request.phone())
                .officeLocation(request.officeLocation())
                .bio(request.bio())
                .tenured(request.tenured())
                .build();

        teacherProfileRepository.save(profile);

        return mapToTeacherResponse(user, profile);
    }

    // ==================== CRUD UTILISATEURS (ADMIN) ====================

    @Transactional(readOnly = true)
    public List<UserListResponse> listAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserList)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserListResponse> listUsersByRole(String roleName) {
        return userRepository.findAll().stream()
                .filter(u -> u.hasRole(roleName))
                .map(this::mapToUserList)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StudentProfileResponse getStudentByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Élève non trouvé"));
        StudentProfile profile = studentProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Profil élève non trouvé"));
        return mapToStudentResponse(user, profile);
    }

    @Transactional(readOnly = true)
    public TeacherProfileResponse getTeacherByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Enseignant non trouvé"));
        TeacherProfile profile = teacherProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Profil enseignant non trouvé"));
        return mapToTeacherResponse(user, profile);
    }

    @Transactional
    public void assignRoles(RoleAssignmentRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        Set<Role> newRoles = new HashSet<>();
        for (String roleName : request.roleNames()) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new IllegalArgumentException("Rôle inconnu: " + roleName));
            newRoles.add(role);
        }

        user.setRoles(newRoles);
        userRepository.save(user);
    }

    @Transactional
    public void toggleUserActive(Long userId, boolean active) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        user.setActive(active);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        // Supprimer les profils associés d'abord
        studentProfileRepository.findByUserEmail(user.getUsername())
                .ifPresent(studentProfileRepository::delete);
        teacherProfileRepository.findByUserEmail(user.getUsername())
                .ifPresent(teacherProfileRepository::delete);

        userRepository.delete(user);
    }

    // ==================== PROFIL CONNECTÉ ====================

    @Transactional(readOnly = true)
    public Object getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        if (user.hasRole(ROLE_ELEVE)) {
            return getStudentByEmail(email);
        } else if (user.hasRole(ROLE_ENSEIGNANT)) {
            return getTeacherByEmail(email);
        } else {
            // Admin / SuperAdmin → profil basique
            return new UserListResponse(
                    user.getId(),
                    user.getPublicUsername(),
                    user.getEmail(),
                    user.isActive(),
                    user.getRoleNames(),
                    user.hasRole(ROLE_SUPERADMIN) ? "SUPERADMIN" : "ADMIN"
            );
        }
    }

    // ==================== GESTION AVATAR ====================

    @Transactional
    public void updateAvatar(String email, String filename) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        // Supprimer l'ancien avatar si existe
        if (user.getAvatarUrl() != null) {
            avatarService.deleteFile(user.getAvatarUrl());
        }

        user.setAvatarUrl(filename);
        userRepository.save(user);
    }

    @Transactional
    public void deleteAvatar(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        if (user.getAvatarUrl() != null) {
            avatarService.deleteFile(user.getAvatarUrl());
            user.setAvatarUrl(null);
            userRepository.save(user);
        }
    }

    @Transactional(readOnly = true)
    public String getAvatarFilename(String email) {
        return userRepository.findByEmail(email)
                .map(User::getAvatarUrl)
                .filter(url -> url != null && !url.isBlank())
                .orElseThrow(() -> new IllegalArgumentException("Aucun avatar défini pour cet utilisateur"));
    }

    // ==================== MAPPERS ====================

    private StudentProfileResponse mapToStudentResponse(User user, StudentProfile profile) {
        return new StudentProfileResponse(
                user.getId(),
                user.getPublicUsername(),
                user.getUsername(),
                user.getAvatarUrl(),
                user.isActive(),
                user.getRoleNames(),
                user.getPermissionNames(),
                profile.getStudentId(),
                profile.getRegistrationNumber(),
                profile.getGradeLevel(),
                profile.getClassName(),
                profile.getSection(),
                profile.getAcademicYear(),
                profile.getBirthDate(),
                profile.getParentName(),
                profile.getParentPhone(),
                profile.getParentEmail(),
                profile.getEmergencyContact(),
                profile.getAddress(),
                profile.getEnrollmentDate(),
                profile.isScholarship()
        );
    }

    private TeacherProfileResponse mapToTeacherResponse(User user, TeacherProfile profile) {
        return new TeacherProfileResponse(
                user.getId(),
                user.getPublicUsername(),
                user.getUsername(),
                user.getAvatarUrl(),
                user.isActive(),
                user.getRoleNames(),
                user.getPermissionNames(),
                profile.getTeacherId(),
                profile.getDepartment(),
                profile.getSpecialization(),
                profile.getSubjects(),
                profile.getHireDate(),
                profile.getQualification(),
                profile.getPhone(),
                profile.getOfficeLocation(),
                profile.getBio(),
                profile.isTenured()
        );
    }

    private UserListResponse mapToUserList(User user) {
        String profileType = "UNKNOWN";
        if (user.hasRole(ROLE_ELEVE)) profileType = "STUDENT";
        else if (user.hasRole(ROLE_ENSEIGNANT)) profileType = "TEACHER";
        else if (user.hasRole(ROLE_ADMIN)) profileType = "ADMIN";
        else if (user.hasRole(ROLE_SUPERADMIN)) profileType = "SUPERADMIN";

        return new UserListResponse(
                user.getId(),
                user.getPublicUsername(),
                user.getUsername(),
                user.isActive(),
                user.getRoleNames(),
                profileType
        );
    }


}