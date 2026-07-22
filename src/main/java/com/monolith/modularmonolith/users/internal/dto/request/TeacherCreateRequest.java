package com.monolith.modularmonolith.users.internal.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

/**
 * DTO pour la création d'un compte enseignant par un administrateur.
 */
public record TeacherCreateRequest(
        @NotBlank @Size(min = 3, max = 50) String username,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 2, max = 100) String firstName,
        @NotBlank @Size(min = 2, max = 100) String lastName,
        @Pattern(regexp = "^\\+?[0-9\\s\\-\\(\\)]{8,20}$") String phone,
        @Pattern(regexp = "^(MALE|FEMALE|OTHER)$") String gender,
        @Past LocalDate birthDate,
        @Size(max = 100) String nationality,

        // === Professionnel ===
        @NotBlank @Size(max = 100) String department,
        @NotBlank @Size(max = 100) String specialization,
        @NotEmpty Set<@Size(max = 100) String> subjects,
        Set<@Size(max = 50) String> classesAssigned,
        @NotNull LocalDate hireDate,
        LocalDate contractEndDate,
        @Pattern(regexp = "^(CDI|CDD|VACATAIRE)$") String contractType,
        @Size(max = 100) String qualification,
        @Size(max = 500) String certifications,
        @Size(max = 100) String officeLocation,
        @Size(max = 100) String officeHours,
        @Size(max = 1000) String bio,
        @Size(max = 500) String researchInterests,
        int yearsOfExperience,
        boolean departmentHead
) {}