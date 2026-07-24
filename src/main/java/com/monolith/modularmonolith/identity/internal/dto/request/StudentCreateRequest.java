package com.monolith.modularmonolith.identity.internal.dto.request;

import com.monolith.modularmonolith.identity.internal.domain.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Set;

@Builder
public record StudentCreateRequest(
        @NotBlank @Size(min = 3, max = 50) String username,
        @NotBlank @Email String email,
        @NotBlank String firstName,
        @NotBlank String lastName,
        String phoneNumber,
        String gender,
        LocalDate dateOfBirth,
        String nationality,
        String language,
        @NotNull Set<Role> roles,

        // Profil étudiant
        @NotBlank String gradeLevel,
        String className,
        String section,
        String academicYear,
        LocalDate enrollmentDate,
        Boolean scholarship,
        String scholarshipType,
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