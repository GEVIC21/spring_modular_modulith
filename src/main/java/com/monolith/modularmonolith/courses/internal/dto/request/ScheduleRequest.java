package com.monolith.modularmonolith.courses.internal.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;

public record ScheduleRequest(
        @NotNull Long courseId,
        @NotNull Long classroomId,
        @NotNull Long teacherId,
        @NotNull DayOfWeek dayOfWeek,
        @NotNull LocalTime startTime,
        @NotNull LocalTime endTime,
        @NotBlank String room,
        @NotBlank String academicYear,
        String semester
) {}