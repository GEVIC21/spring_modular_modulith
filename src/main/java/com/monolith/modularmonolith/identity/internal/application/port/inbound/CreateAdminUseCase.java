package com.monolith.modularmonolith.identity.internal.application.port.inbound;

import com.monolith.modularmonolith.identity.internal.dto.request.AdminCreateRequest;
import com.monolith.modularmonolith.identity.internal.dto.response.UserProfileResponse;

/**
 * Port inbound : Création d'un compte administrateur.
 * Réservé au SUPER_ADMIN.
 */
public interface CreateAdminUseCase {

    UserProfileResponse execute(AdminCreateRequest request);
}