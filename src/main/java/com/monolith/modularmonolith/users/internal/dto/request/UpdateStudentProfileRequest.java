package com.monolith.modularmonolith.users.internal.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

public record UpdateStudentProfileRequest(
        @Size(max = 100) String firstName,
        @Size(max = 100) String lastName,
        @Pattern(regexp = "^\\+?[0-9\\s\\-\\(\\)]{8,20}$", message = "Numéro de téléphone invalide") String phone,
        @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "Genre invalide") String gender,
        @Past(message = "La date de naissance doit être dans le passé") LocalDate birthDate,
        @Size(max = 100) String placeOfBirth,
        @Size(max = 100) String nationality,
        @Size(max = 5) String language,
        @Size(max = 50) String timezone,

        // Champs élève
        @Size(max = 50) String parentName,
        @Email(message = "Email parent invalide") String parentEmail,
        @Pattern(regexp = "^\\+?[0-9\\s\\-\\(\\)]{8,20}$") String parentPhone,
        @Size(max = 100) String emergencyContact,
        @Pattern(regexp = "^\\+?[0-9\\s\\-\\(\\)]{8,20}$") String emergencyContactPhone,
        @Size(max = 255) String address,
        @Size(max = 100) String city,
        @Size(max = 20) String postalCode,
        @Size(max = 100) String country,
        @Size(max = 10) String bloodGroup,
        @Size(max = 500) String allergies,
        @Size(max = 1000) String medicalNotes,
        Set<@Size(max = 100) String> extracurricularActivities
) {}