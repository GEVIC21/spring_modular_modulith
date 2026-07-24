package com.monolith.modularmonolith.identity.internal.dto.response;

import lombok.Builder;

import java.util.List;
import java.util.Set;

@Builder
public record UserProfileResponse(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        String gender,
        String dateOfBirth,
        String nationality,
        String language,
        String avatarUrl,        // ← DOIT EXISTER
        boolean active,
        boolean emailVerified,
        String lastLoginAt,
        String profileType,
        Set<String> roles,
        StudentInfo studentInfo,
        TeacherInfo teacherInfo,
        AdminInfo adminInfo
) {
    @Builder public record StudentInfo(String studentId, String gradeLevel, String className, String section, String academicYear, String enrollmentDate, Boolean scholarship, String parentName, String parentPhone, String parentEmail, String emergencyContact, String emergencyPhone, String address, String city, String postalCode, String country, String bloodGroup, String allergies, String medicalNotes, String extracurricularActivities) {}
    @Builder public record TeacherInfo(String teacherId, String employeeId, String department, List<String> subjects, List<String> classesAssigned, String qualification, String hireDate, String specialization, String officeLocation, String officeHours) {}
    @Builder public record AdminInfo(String adminId, String department, String accessLevel, List<String> permissions, String hireDate, String jobTitle, String officeLocation) {}
}