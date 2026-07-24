package com.monolith.modularmonolith.identity.internal.mapper;

import com.monolith.modularmonolith.identity.internal.domain.model.*;
import com.monolith.modularmonolith.identity.internal.dto.response.UserProfileResponse;
import com.monolith.modularmonolith.identity.internal.dto.response.UserSummaryResponse;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserProfileMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    /** ─── FULL PROFILE ─── */
    public UserProfileResponse toResponse(User user) {
        if (user == null) return null;

        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth() != null ? user.getDateOfBirth().format(DATE_FORMATTER) : null)
                .nationality(user.getNationality())
                .language(user.getLanguage())
                .avatarUrl(buildAvatarUrl(user.getAvatarFilename()))
                .active(user.isActive())
                .emailVerified(user.isEmailVerified())
                .lastLoginAt(user.getLastLoginAt() != null ? user.getLastLoginAt().toString() : null)
                .profileType(user.getProfileType() != null ? user.getProfileType().name() : null)
                .roles(user.getRoles().stream().map(Role::name).collect(Collectors.toSet()))
                .studentInfo(toStudentInfo(user.getStudentProfile()))
                .teacherInfo(toTeacherInfo(user.getTeacherProfile()))
                .adminInfo(toAdminInfo(user.getAdminProfile()))
                .build();
    }

    /** ─── SUMMARY (liste / recherche) ─── */
    public UserSummaryResponse toUserSummaryResponse(User user) {
        if (user == null) return null;

        return UserSummaryResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                // 👇 AJOUTEZ CETTE LIGNE pour alimenter le fullName
                .fullName(user.getFirstName() + " " + user.getLastName())
                .profileType(user.getProfileType() != null ? ProfileType.valueOf(user.getProfileType().name()) : null)
                .active(user.isActive())
                .avatarUrl(buildAvatarUrl(user.getAvatarFilename()))
                .roles(user.getRoles()) // Déjà ajouté à l'étape précédente
                .build();
    }

    private String buildAvatarUrl(String filename) {
        if (filename == null || filename.isBlank()) return null;
        if (filename.startsWith("http")) return filename;
        return "http://localhost:8080/api/v1/public/avatars/" + filename;
    }

    private UserProfileResponse.StudentInfo toStudentInfo(StudentProfile p) {
        if (p == null) return null;
        return UserProfileResponse.StudentInfo.builder()
                .studentId(p.getStudentId())
                .gradeLevel(p.getGradeLevel())
                .className(p.getClassName())
                .section(p.getSection())
                .academicYear(p.getAcademicYear())
                .enrollmentDate(p.getEnrollmentDate() != null ? p.getEnrollmentDate().format(DATE_FORMATTER) : null)
                .scholarship(p.getScholarship())
                .parentName(p.getParentName())
                .parentPhone(p.getParentPhone())
                .parentEmail(p.getParentEmail())
                .emergencyContact(p.getEmergencyContact())
                .emergencyPhone(p.getEmergencyPhone())
                .address(p.getAddress())
                .city(p.getCity())
                .postalCode(p.getPostalCode())
                .country(p.getCountry())
                .bloodGroup(p.getBloodGroup())
                .allergies(p.getAllergies())
                .medicalNotes(p.getMedicalNotes())
                .extracurricularActivities(join(p.getExtracurricularActivities()))
                .build();
    }

    private UserProfileResponse.TeacherInfo toTeacherInfo(TeacherProfile p) {
        if (p == null) return null;
        return UserProfileResponse.TeacherInfo.builder()
                .teacherId(p.getTeacherId())
                .employeeId(p.getEmployeeId())
                .department(p.getDepartment())
                .subjects(toList(p.getSubjects()))
                .classesAssigned(toList(p.getClassesAssigned()))
                .qualification(p.getQualification())
                .hireDate(p.getHireDate() != null ? p.getHireDate().format(DATE_FORMATTER) : null)
                .specialization(p.getSpecialization())
                .officeLocation(p.getOfficeLocation())
                .officeHours(p.getOfficeHours())
                .build();
    }

    private UserProfileResponse.AdminInfo toAdminInfo(AdminProfile p) {
        if (p == null) return null;
        return UserProfileResponse.AdminInfo.builder()
                .adminId(p.getAdminId())
                .department(p.getDepartment())
                .accessLevel(p.getAccessLevel())
                .permissions(parsePermissions(p.getPermissions()))
                .hireDate(p.getHireDate() != null ? p.getHireDate().format(DATE_FORMATTER) : null)
                .jobTitle(p.getJobTitle())
                .officeLocation(p.getOfficeLocation())
                .build();
    }

    private List<String> toList(Collection<String> collection) {
        if (collection == null) return null;
        return collection.stream().toList();
    }

    private String join(Collection<String> collection) {
        if (collection == null || collection.isEmpty()) return null;
        return String.join(", ", collection);
    }

    private List<String> parsePermissions(String permissions) {
        if (permissions == null || permissions.isBlank()) return null;
        return Arrays.stream(permissions.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}