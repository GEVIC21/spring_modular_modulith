package com.monolith.modularmonolith.users.internal.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record UpdateAdminProfileRequest(
        @Size(max = 100) String firstName,
        @Size(max = 100) String lastName,
        @Pattern(regexp = "^\\+?[0-9\\s\\-\\(\\)]{8,20}$") String phone,
        @Pattern(regexp = "^(MALE|FEMALE|OTHER)$") String gender,
        @Past LocalDate birthDate,
        @Size(max = 100) String nationality,
        @Size(max = 5) String language,
        @Size(max = 50) String timezone,

        // Champs admin
        @Size(max = 100) String department,
        @Size(max = 100) String jobTitle,
        @Size(max = 100) String officeLocation,
        @Size(max = 50) String officePhone
) {}