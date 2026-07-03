package com.monolith.modularmonolith.courses.internal.dto.response;


import java.util.List;

public record CourseResponse(
        Long id,
        String code,
        String name,
        String description,
        Integer credits,
        String department,
        String gradeLevel,
        Boolean active,
        List<TeacherSummary> teachers
) {
    public record TeacherSummary(Long id, String username, String email) {}
}