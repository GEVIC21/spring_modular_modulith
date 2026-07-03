package com.monolith.modularmonolith.grades.internal.dto;

import com.monolith.modularmonolith.grades.internal.model.GradeType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GradeRequest(
        @NotNull(message = "L'ID de l'élève est obligatoire")
        Long studentId,

        @NotNull(message = "L'ID du cours est obligatoire")
        Long courseId,

        @NotNull(message = "L'ID de l'enseignant est obligatoire")
        Long teacherId,

        @NotNull @DecimalMin("0.00") @DecimalMax("999.99") @Digits(integer = 3, fraction = 2)
        BigDecimal value,

        @NotNull @DecimalMin("0.01") @Digits(integer = 3, fraction = 2)
        BigDecimal maxValue,

        @NotNull
        GradeType type,

        @NotNull
        LocalDate gradeDate,

        String semester,

        @NotBlank @Size(max = 10)
        String academicYear,

        @Size(max = 500)
        String comment
) {}