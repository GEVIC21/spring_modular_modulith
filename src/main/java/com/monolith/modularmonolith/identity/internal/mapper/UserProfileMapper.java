package com.monolith.modularmonolith.identity.internal.mapper;

import com.monolith.modularmonolith.identity.internal.domain.model.*;
import com.monolith.modularmonolith.identity.internal.dto.response.UserProfileResponse;
import com.monolith.modularmonolith.identity.internal.dto.response.UserProfileResponse.*;
import com.monolith.modularmonolith.identity.internal.dto.response.UserSummaryResponse;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserProfileMapper {

    public UserProfileResponse toUserProfileResponse(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getFullName(),
                user.isActive(),
                user.isEmailVerified(),
                user.getRoles() != null ? user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()) : null,
                user.getProfileType() != null ? user.getProfileType().name() : null,
                user.getAvatarUrl(),
                null, // thumbnailUrl - computed
                user.getPhone(),
                user.getGender(),
                user.getBirthDate(),
                user.getNationality(),
                user.getLanguage(),
                user.getTimezone(),
                user.getLastLoginAt(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                mapStudentInfo(user.getStudentProfile()),
                mapTeacherInfo(user.getTeacherProfile()),
                mapAdminInfo(user.getAdminProfile())
        );
    }

    public UserSummaryResponse toUserSummaryResponse(User user) {
        return new UserSummaryResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getFullName(),
                user.getProfileType() != null ? user.getProfileType().name() : null,
                user.getStudentProfile() != null ? user.getStudentProfile().getGradeLevel() : null,
                user.getTeacherProfile() != null ? user.getTeacherProfile().getDepartment() : null,
                user.isActive(),
                user.getAvatarUrl(),
                user.getRoles() != null ? user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()) : null,
                user.getCreatedAt()
        );
    }

    private StudentInfo mapStudentInfo(StudentProfile p) {
        if (p == null) return null;
        return new StudentInfo(
                p.getStudentId(), p.getRegistrationNumber(), p.getGradeLevel(),
                p.getClassName(), p.getSection(), p.getAcademicYear(),
                p.getPlaceOfBirth(), p.getParentName(), p.getParentPhone(),
                p.getParentEmail(), p.getEmergencyContact(), p.getEmergencyContactPhone(),
                p.getAddress(), p.getCity(), p.getPostalCode(), p.getCountry(),
                p.getEnrollmentDate(), p.getGraduationDate(), p.isScholarship(),
                p.getScholarshipType(), p.getGpa(), p.getAttendanceRate(),
                p.getBloodGroup(), p.getAllergies(), p.getMedicalNotes(),
                p.getExtracurricularActivities()
        );
    }

    private TeacherInfo mapTeacherInfo(TeacherProfile p) {
        if (p == null) return null;
        return new TeacherInfo(
                p.getTeacherId(), p.getEmployeeId(), p.getDepartment(),
                p.getSpecialization(), p.getSubjects(), p.getClassesAssigned(),
                p.getHireDate(), p.getContractEndDate(), p.getContractType(),
                p.getQualification(), p.getCertifications(), p.getPhone(),
                p.getOfficeLocation(), p.getOfficeHours(), p.getBio(),
                p.getResearchInterests(), p.getYearsOfExperience(),
                p.isTenured(), p.isDepartmentHead()
        );
    }

    private AdminInfo mapAdminInfo(AdminProfile p) {
        if (p == null) return null;
        return new AdminInfo(
                p.getAdminId(), p.getDepartment(), p.getJobTitle(),
                p.getHireDate(), p.getAccessLevel(), p.getManagedModules(),
                p.isCanManageUsers(), p.isCanManageFinances(),
                p.isCanManageAcademics(), p.getOfficePhone(), p.getOfficeLocation()
        );
    }
}