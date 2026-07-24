package com.monolith.modularmonolith.identity.internal.dto.request;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record UpdateTeacherProfileRequest(
        String firstName,
        String lastName,
        String phoneNumber,
        String department,
        List<String> subjects,
        List<String> classesAssigned,
        String qualification,
        LocalDate hireDate,
        String specialization,
        String officeLocation,
        String officeHours
) {}