package com.monolith.modularmonolith.authentication.internal.application.port.inbound;

import com.monolith.modularmonolith.authentication.internal.dto.AuthResponse;
import com.monolith.modularmonolith.authentication.internal.dto.LoginRequest;

public interface AuthenticateUseCase {

    AuthResponse execute(LoginRequest request);
}