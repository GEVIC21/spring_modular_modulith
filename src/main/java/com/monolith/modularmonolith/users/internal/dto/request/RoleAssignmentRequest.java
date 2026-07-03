package com.monolith.modularmonolith.users.internal.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record RoleAssignmentRequest(
        @NotNull(message = "L'ID utilisateur est obligatoire")
        Long userId,

        @NotEmpty(message = "Au moins un rôle est requis")
        Set<String> roleNames
) {}