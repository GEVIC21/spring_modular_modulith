package com.monolith.modularmonolith.users.internal.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

/**
 * DTO pour la création d'un compte élève par un administrateur.
 * Le mot de passe est généré automatiquement et envoyé par email.
 */
public record StudentCreateRequest(
        @NotBlank @Size(min = 3, max = 50) String username,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 2, max = 100) String firstName,
        @NotBlank @Size(min = 2, max = 100) String lastName,
        @Pattern(regexp = "^\\+?[0-9\\s\\-\\(\\)]{8,20}$") String phone,
        @Pattern(regexp = "^(MALE|FEMALE|OTHER)$") String gender,
        @Past LocalDate birthDate,
        @Size(max = 100) String placeOfBirth,
        @Size(max = 100) String nationality,
        @Size(max = 5) String language,

        // === Champs académiques ===
        @NotBlank String gradeLevel,
        @NotBlank String className,
        @Size(max = 50) String section,
        @NotBlank String academicYear,
        LocalDate enrollmentDate,
        boolean scholarship,
        @Size(max = 100) String scholarshipType,

        // === Contact parent/tuteur ===
        @NotBlank @Size(max = 100) String parentName,
        @Email String parentEmail,
        @Pattern(regexp = "^\\+?[0-9\\s\\-\\(\\)]{8,20}$") String parentPhone,
        @Size(max = 100) String emergencyContact,
        @Pattern(regexp = "^\\+?[0-9\\s\\-\\(\\)]{8,20}$") String emergencyContactPhone,

        // === Adresse ===
        @Size(max = 255) String address,
        @Size(max = 100) String city,
        @Size(max = 20) String postalCode,
        @Size(max = 100) String country,

        // === Médical ===
        @Size(max = 10) String bloodGroup,
        @Size(max = 500) String allergies,
        @Size(max = 1000) String medicalNotes,

        // === Activités ===
        Set<@Size(max = 100) String> extracurricularActivities
) {}