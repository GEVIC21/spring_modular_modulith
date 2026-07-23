package com.monolith.modularmonolith.identity.internal.application.port.inbound;

import com.monolith.modularmonolith.identity.internal.domain.model.ProfileType;
import com.monolith.modularmonolith.identity.internal.dto.response.UserSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Port inbound : Liste paginée des utilisateurs avec filtres.
 */
public interface ListUsersUseCase {

    Page<UserSummaryResponse> execute(
            Pageable pageable,
            ProfileType profileType,
            String search,
            Boolean active);
}