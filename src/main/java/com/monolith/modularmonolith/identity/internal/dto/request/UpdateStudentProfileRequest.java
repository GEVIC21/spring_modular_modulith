package com.monolith.modularmonolith.identity.internal.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Set;

@Builder
public record UpdateStudentProfileRequest(
        String firstName,
        String lastName,
        String phoneNumber,
        String gender,
        LocalDate dateOfBirth,
        String nationality,
        String language,

        // Profil étudiant
        String gradeLevel,
        String className,
        LocalDate enrollmentDate,
        String parentName,
        String parentPhone,
        @Email String parentEmail,
        String emergencyContact,
        String emergencyPhone,
        String address,
        String city,
        String postalCode,
        String country,
        String bloodGroup,
        String allergies,
        String medicalNotes,
        Set<String> extracurricularActivities
) {}