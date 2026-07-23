package com.monolith.modularmonolith.identity.internal.application.port.inbound;

import com.monolith.modularmonolith.identity.internal.dto.response.UserProfileResponse;

/**
 * Port inbound : Récupération du profil complet d'un utilisateur.
 */
public interface GetUserProfileUseCase {

    UserProfileResponse byEmail(String email);

    UserProfileResponse byId(Long userId);
}