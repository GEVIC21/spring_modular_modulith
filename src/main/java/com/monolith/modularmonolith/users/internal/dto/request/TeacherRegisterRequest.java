package com.monolith.modularmonolith.users.internal.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

public record TeacherRegisterRequest(
        @NotBlank(message = "Le nom d'utilisateur est obligatoire")
        @Size(min = 3, max = 20, message = "Entre 3 et 20 caractères")
        String username,

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Email invalide")
        String email,

        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 6, message = "Au moins 6 caractères")
        String password,

        @NotBlank(message = "L'ID enseignant est obligatoire")
        String teacherId,

        String department,
        String specialization,
        Set<String> subjects,
        LocalDate hireDate,
        String qualification,
        String phone,
        String officeLocation,
        String bio,
        boolean tenured
) {}