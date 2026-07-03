package com.monolith.modularmonolith.grades.internal.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ReportCardResponse(
        Long id,
        Long studentId,
        String studentName,
        String academicYear,
        String semester,
        BigDecimal overallAverage,
        Integer rank,
        Integer totalStudents,
        Long classroomId,
        String principalComment,
        List<GradeResponse> grades,
        boolean active,
        LocalDateTime generatedAt
) {}