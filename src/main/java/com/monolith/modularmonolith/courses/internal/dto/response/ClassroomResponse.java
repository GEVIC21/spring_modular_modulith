package com.monolith.modularmonolith.courses.internal.dto.response;

public record ClassroomResponse(
        Long id,
        String name,
        String gradeLevel,
        String section,
        String academicYear,
        String roomNumber,
        Integer capacity,
        Boolean active,
        TeacherSummary homeroomTeacher,
        Integer studentCount,
        Integer courseCount
) {
    public record TeacherSummary(Long id, String username, String email) {}
}