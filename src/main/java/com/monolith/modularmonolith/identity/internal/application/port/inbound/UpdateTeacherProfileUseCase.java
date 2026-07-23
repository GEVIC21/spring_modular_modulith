package com.monolith.modularmonolith.identity.internal.application.port.inbound;

import com.monolith.modularmonolith.identity.internal.dto.request.UpdateTeacherProfileRequest;
import com.monolith.modularmonolith.identity.internal.dto.response.UserProfileResponse;

/**
 * Port inbound : Mise à jour du profil enseignant.
 */
public interface UpdateTeacherProfileUseCase {

    UserProfileResponse execute(String email, UpdateTeacherProfileRequest request);
}