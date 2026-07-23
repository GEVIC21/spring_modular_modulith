package com.monolith.modularmonolith.authentication.internal.application.port.inbound;

import com.monolith.modularmonolith.authentication.internal.dto.LoginRequest;
import com.monolith.modularmonolith.authentication.internal.dto.AuthResponse;

public interface AuthenticateUseCase {
    AuthResponse execute(LoginRequest request);
}