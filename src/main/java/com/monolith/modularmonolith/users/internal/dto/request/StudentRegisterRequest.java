package com.monolith.modularmonolith.users.internal.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record StudentRegisterRequest(
        @NotBlank(message = "Le nom d'utilisateur est obligatoire")
        @Size(min = 3, max = 20, message = "Entre 3 et 20 caractères")
        String username,

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Email invalide")
        String email,

        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 6, message = "Au moins 6 caractères")
        String password,

        @NotBlank(message = "L'ID étudiant est obligatoire")
        String studentId,

        String registrationNumber,
        String gradeLevel,
        String className,
        String section,
        String academicYear,

        @NotNull(message = "La date de naissance est obligatoire")
        LocalDate birthDate,

        @NotBlank(message = "Le nom du parent est obligatoire")
        String parentName,

        @NotBlank(message = "Le téléphone du parent est obligatoire")
        String parentPhone,

        String parentEmail,
        String emergencyContact,
        String address,
        LocalDate enrollmentDate,
        boolean scholarship
) {}