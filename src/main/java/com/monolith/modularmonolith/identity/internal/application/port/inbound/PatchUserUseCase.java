package com.monolith.modularmonolith.identity.internal.application.port.inbound;

import com.monolith.modularmonolith.identity.internal.dto.request.UserStatusUpdateRequest;
import com.monolith.modularmonolith.identity.internal.dto.response.UserProfileResponse;

/**
 * Port inbound : Mise à jour partielle d'un utilisateur par un administrateur.
 */
public interface PatchUserUseCase {

    UserProfileResponse execute(Long userId, UserStatusUpdateRequest request);
}