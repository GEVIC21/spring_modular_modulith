package com.monolith.modularmonolith.authentication.internal.adapter.web;

import com.monolith.modularmonolith.authentication.internal.application.port.inbound.AuthenticateUseCase;
import com.monolith.modularmonolith.authentication.internal.application.port.inbound.ChangePasswordUseCase;
import com.monolith.modularmonolith.authentication.internal.dto.AuthResponse;
import com.monolith.modularmonolith.authentication.internal.dto.LoginRequest;
import com.monolith.modularmonolith.authentication.internal.dto.PasswordChangeRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticateUseCase authenticateUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticateUseCase.execute(request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody PasswordChangeRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        changePasswordUseCase.execute(email, request);
        return ResponseEntity.noContent().build();
    }
}