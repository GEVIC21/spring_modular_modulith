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
public record AdminCreateRequest(
        @NotBlank @Size(min = 3, max = 50) String username,
        @NotBlank @Email String email,
        @NotBlank String firstName,
        @NotBlank String lastName,
        String phoneNumber,
        @NotNull Set<Role> roles,

        // Profil administrateur
        String department,
        String accessLevel,
        String permissions,
        LocalDate hireDate,
        String jobTitle,
        String officeLocation
) {}