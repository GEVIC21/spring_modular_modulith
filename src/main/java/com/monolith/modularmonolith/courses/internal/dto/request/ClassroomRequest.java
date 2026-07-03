package com.monolith.modularmonolith.courses.internal.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClassroomRequest(
        @NotBlank String name,
        @NotBlank String gradeLevel,
        String section,
        @NotBlank String academicYear,
        String roomNumber,
        @NotNull @Min(1) Integer capacity
) {}