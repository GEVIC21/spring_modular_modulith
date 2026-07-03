package com.monolith.modularmonolith.courses.internal.dto.request;


import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

public record EnrollStudentsRequest(
        @NotEmpty Set<Long> studentIds
) {}