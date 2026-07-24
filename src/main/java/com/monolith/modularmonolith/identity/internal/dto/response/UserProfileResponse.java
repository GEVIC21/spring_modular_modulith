package com.monolith.modularmonolith.identity.internal.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.monolith.modularmonolith.identity.internal.domain.model.ProfileType;
import com.monolith.modularmonolith.identity.internal.domain.model.Role;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Builder
@JsonInclude(NON_NULL)
public record UserProfileResponse(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        String fullName,
        String phoneNumber,
        String avatarFilename,
        String avatarUrl,
        boolean active,
        boolean emailVerified,
        ProfileType profileType,
        Set<Role> roles,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        StudentInfo studentInfo,
        TeacherInfo teacherInfo,
        AdminInfo adminInfo
) {

    @Builder
    public record StudentInfo(
            String studentId,
            String gradeLevel,
            String className,
            String enrollmentDate,
            String parentName,
            String parentPhone,
            String parentEmail
    ) {}

    @Builder
    public record TeacherInfo(
            String teacherId,
            String employeeId,
            String department,
            List<String> subjects,
            List<String> classesAssigned,
            String qualification,
            String hireDate,
            String specialization,
            String officeLocation,
            String officeHours
    ) {}

    @Builder
    public record AdminInfo(
            String adminId,
            String department,
            String accessLevel,
            String permissions,
            String hireDate,
            String jobTitle,
            String officeLocation
    ) {}
}