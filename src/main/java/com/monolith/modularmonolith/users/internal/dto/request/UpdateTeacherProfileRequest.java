package com.monolith.modularmonolith.users.internal.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

public record UpdateTeacherProfileRequest(
        // === Champs base utilisateur ===
        @Size(max = 100) String firstName,
        @Size(max = 100) String lastName,
        @Pattern(regexp = "^\\+?[0-9\\s\\-\\(\\)]{8,20}$") String phone,
        @Pattern(regexp = "^(MALE|FEMALE|OTHER)$") String gender,
        @Past LocalDate birthDate,
        @Size(max = 100) String nationality,
        @Size(max = 5) String language,
        @Size(max = 50) String timezone,

        // === Champs enseignant ===
        @Size(max = 100) String department,
        @Size(max = 100) String specialization,
        Set<@Size(max = 100) String> subjects,
        Set<@Size(max = 100) String> classesAssigned,
        @Size(max = 500) String bio,
        @Size(max = 500) String researchInterests,
        @Size(max = 100) String officeLocation,
        @Size(max = 100) String officeHours,
        @Size(max = 500) String certifications,
        @Size(max = 100) String qualification,
        @Pattern(regexp = "^(CDI|CDD|VACATAIRE)$") String contractType,
        @Min(0) @Max(60) Integer yearsOfExperience,
        Boolean departmentHead
) {}