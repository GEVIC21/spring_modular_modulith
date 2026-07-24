package com.monolith.modularmonolith.identity.internal.dto.request;

import com.monolith.modularmonolith.identity.internal.domain.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Builder
public record TeacherCreateRequest(
        @NotBlank @Size(min = 3, max = 50) String username,
        @NotBlank @Email String email,
        @NotBlank String firstName,
        @NotBlank String lastName,
        String phoneNumber,
        @NotNull Set<Role> roles,

        // Profil enseignant
        @NotBlank String employeeId,
        String department,
        List<String> subjects,
        List<String> classesAssigned,
        String qualification,
        LocalDate hireDate,
        String specialization,
        String officeLocation,
        String officeHours
) {}