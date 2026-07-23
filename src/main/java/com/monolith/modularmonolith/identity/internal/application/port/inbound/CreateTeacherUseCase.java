package com.monolith.modularmonolith.identity.internal.application.port.inbound;

import com.monolith.modularmonolith.identity.internal.dto.request.TeacherCreateRequest;
import com.monolith.modularmonolith.identity.internal.dto.response.UserProfileResponse;

/**
 * Port inbound : Création d'un compte enseignant.
 */
public interface CreateTeacherUseCase {

    UserProfileResponse execute(TeacherCreateRequest request);
}