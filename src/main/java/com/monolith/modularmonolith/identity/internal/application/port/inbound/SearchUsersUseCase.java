package com.monolith.modularmonolith.identity.internal.application.port.inbound;

import com.monolith.modularmonolith.identity.internal.dto.response.UserSummaryResponse;

import java.util.List;

/**
 * Port inbound : Recherche avancée multi-critères d'utilisateurs.
 */
public interface SearchUsersUseCase {

    List<UserSummaryResponse> execute(SearchUsersQuery query);

    /**
     * Query object pour la recherche avancée.
     */
    record SearchUsersQuery(
            String searchTerm,
            String profileType,
            Boolean active,
            String sortBy,
            String sortDirection,
            Integer limit
    ) {
    }
}