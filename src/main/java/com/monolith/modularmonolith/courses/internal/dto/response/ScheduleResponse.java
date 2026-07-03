package com.monolith.modularmonolith.courses.internal.dto.response;


import java.time.DayOfWeek;
import java.time.LocalTime;

public record ScheduleResponse(
        Long id,
        CourseSummary course,
        ClassroomSummary classroom,
        TeacherSummary teacher,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        String room,
        String academicYear,
        String semester,
        Boolean active
) {
    public record CourseSummary(Long id, String code, String name) {}
    public record ClassroomSummary(Long id, String name, String gradeLevel, String section) {}
    public record TeacherSummary(Long id, String username, String email) {}
}