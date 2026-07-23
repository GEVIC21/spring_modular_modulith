package com.monolith.modularmonolith.authentication.internal.adapter.web;

import com.monolith.modularmonolith.authentication.internal.application.port.inbound.AuthenticateUseCase;
import com.monolith.modularmonolith.authentication.internal.dto.AuthResponse;
import com.monolith.modularmonolith.authentication.internal.dto.LoginRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticateUseCase authenticateUseCase;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticateUseCase.execute(request));
    }
}