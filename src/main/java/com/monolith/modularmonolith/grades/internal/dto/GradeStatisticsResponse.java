package com.monolith.modularmonolith.grades.internal.dto;

import java.math.BigDecimal;

public record GradeStatisticsResponse(
        Long courseId,
        String courseName,
        BigDecimal classAverage,
        BigDecimal maxGrade,
        BigDecimal minGrade,
        Long totalGrades
) {}