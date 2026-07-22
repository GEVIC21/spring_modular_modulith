package com.monolith.modularmonolith.auth.internal.service;

import com.monolith.modularmonolith.auth.internal.dto.request.LoginRequest;
import com.monolith.modularmonolith.auth.internal.dto.response.AuthResponse;

public interface AuthenticationService {

    AuthResponse login(LoginRequest request);

}