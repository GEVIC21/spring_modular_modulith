package com.monolith.modularmonolith.identity.internal.mapper;

import com.monolith.modularmonolith.identity.internal.domain.model.*;
import com.monolith.modularmonolith.identity.internal.dto.response.UserProfileResponse;
import com.monolith.modularmonolith.identity.internal.dto.response.UserSummaryResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class UserProfileMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public UserProfileResponse toUserProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .avatarFilename(user.getAvatarFilename())
                .active(user.isActive())
                .emailVerified(user.isEmailVerified())
                .profileType(user.getProfileType())
                .roles(user.getRoles())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .studentInfo(mapStudent(user.getStudentProfile()))
                .teacherInfo(mapTeacher(user.getTeacherProfile()))
                .adminInfo(mapAdmin(user.getAdminProfile()))
                .build();
    }

    public UserSummaryResponse toUserSummaryResponse(User user) {
        return UserSummaryResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .profileType(user.getProfileType())
                .roles(user.getRoles())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private UserProfileResponse.StudentInfo mapStudent(StudentProfile p) {
        if (p == null) return null;
        return UserProfileResponse.StudentInfo.builder()
                .studentId(p.getStudentId())
                .gradeLevel(p.getGradeLevel())
                .className(p.getClassName())
                .enrollmentDate(format(p.getEnrollmentDate()))
                .parentName(p.getParentName())
                .parentPhone(p.getParentPhone())
                .parentEmail(p.getParentEmail())
                .build();
    }

    private UserProfileResponse.TeacherInfo mapTeacher(TeacherProfile p) {
        if (p == null) return null;
        return UserProfileResponse.TeacherInfo.builder()
                .teacherId(p.getTeacherId())
                .employeeId(p.getEmployeeId())
                .department(p.getDepartment())
                .subjects(p.getSubjects())
                .classesAssigned(p.getClassesAssigned())
                .qualification(p.getQualification())
                .hireDate(format(p.getHireDate()))
                .specialization(p.getSpecialization())
                .officeLocation(p.getOfficeLocation())
                .officeHours(p.getOfficeHours())
                .build();
    }

    private UserProfileResponse.AdminInfo mapAdmin(AdminProfile p) {
        if (p == null) return null;
        return UserProfileResponse.AdminInfo.builder()
                .adminId(p.getAdminId())
                .department(p.getDepartment())
                .accessLevel(p.getAccessLevel())
                .permissions(p.getPermissions())
                .hireDate(format(p.getHireDate()))
                .jobTitle(p.getJobTitle())
                .officeLocation(p.getOfficeLocation())
                .build();
    }

    private String format(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : null;
    }
}