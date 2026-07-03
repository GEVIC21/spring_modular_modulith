package com.monolith.modularmonolith.grades.internal.dto;

import com.monolith.modularmonolith.grades.internal.model.GradeType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record GradeResponse(
        Long id,
        Long studentId,
        String studentName,
        Long courseId,
        Long teacherId,
        String teacherName,
        BigDecimal value,
        BigDecimal maxValue,
        GradeType type,
        LocalDate gradeDate,
        String semester,
        String academicYear,
        String comment,
        boolean active,
        LocalDateTime createdAt
) {}