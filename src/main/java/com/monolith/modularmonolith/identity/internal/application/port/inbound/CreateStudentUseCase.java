package com.monolith.modularmonolith.identity.internal.application.port.inbound;

import com.monolith.modularmonolith.identity.internal.dto.request.StudentCreateRequest;
import com.monolith.modularmonolith.identity.internal.dto.response.UserProfileResponse;

/**
 * Port inbound : Création d'un compte élève.
 */
public interface CreateStudentUseCase {

    UserProfileResponse execute(StudentCreateRequest request);
}