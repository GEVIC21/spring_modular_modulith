package com.monolith.modularmonolith.identity.internal.application.port.inbound;

import com.monolith.modularmonolith.identity.internal.dto.response.UserStatistics;

/**
 * Port inbound : Statistiques globales des utilisateurs.
 */
public interface GetUserStatisticsUseCase {

    UserStatistics execute();
}