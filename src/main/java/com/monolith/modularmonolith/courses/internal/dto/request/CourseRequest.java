package com.monolith.modularmonolith.courses.internal.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CourseRequest(
        @NotBlank String code,
        @NotBlank String name,
        String description,
        @NotNull @Min(1) Integer credits,
        @NotBlank String department,
        @NotBlank String gradeLevel
) {}