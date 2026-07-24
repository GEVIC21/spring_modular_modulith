package com.monolith.modularmonolith.identity.internal.dto.response;

import lombok.Builder;

/**
 * DTO de statistiques globales des utilisateurs.
 */
@Builder
public record UserStatistics(
        long totalUsers,
        long totalStudents,
        long totalTeachers,
        long totalAdmins,
        long totalSuperAdmins,
        long totalParents,
        long activeUsers,
        long inactiveUsers
) {
}