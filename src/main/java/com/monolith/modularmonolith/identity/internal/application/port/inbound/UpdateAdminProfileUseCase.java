package com.monolith.modularmonolith.identity.internal.application.port.inbound;

import com.monolith.modularmonolith.identity.internal.dto.request.UpdateAdminProfileRequest;
import com.monolith.modularmonolith.identity.internal.dto.response.UserProfileResponse;

/**
 * Port inbound : Mise à jour du profil administrateur.
 */
public interface UpdateAdminProfileUseCase {

    UserProfileResponse execute(String email, UpdateAdminProfileRequest request);
}