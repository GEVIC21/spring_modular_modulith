package com.monolith.modularmonolith.identity.internal.application.port.inbound;

import com.monolith.modularmonolith.identity.internal.dto.request.UpdateStudentProfileRequest;
import com.monolith.modularmonolith.identity.internal.dto.response.UserProfileResponse;

/**
 * Port inbound : Mise à jour du profil élève.
 */
public interface UpdateStudentProfileUseCase {

    UserProfileResponse execute(String email, UpdateStudentProfileRequest request);
}