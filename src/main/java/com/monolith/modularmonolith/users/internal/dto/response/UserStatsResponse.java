package com.monolith.modularmonolith.users.internal.dto.response;

public record UserStatsResponse(
        long totalUsers,
        long totalStudents,
        long totalTeachers,
        long totalAdmins,
        long activeUsers,
        long inactiveUsers,
        long newUsersThisMonth,
        long usersByGradeLevel  // Ou un Map<String, Long> pour le détail
) {}