package com.monolith.modularmonolith.grades.internal.dto;

import jakarta.validation.constraints.*;

import java.util.Set;

public record ReportCardRequest(
        @NotNull
        Long studentId,

        @NotBlank @Size(max = 10)
        String academicYear,

        @Size(max = 10)
        String semester,

        Set<Long> gradeIds,

        Long classroomId,

        @Size(max = 1000)
        String principalComment
) {}