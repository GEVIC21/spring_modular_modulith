package com.monolith.modularmonolith.auth.internal.controller;

import com.monolith.modularmonolith.auth.internal.dto.request.LoginRequest;
import com.monolith.modularmonolith.auth.internal.dto.response.AuthResponse;
import com.monolith.modularmonolith.auth.internal.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authenticationService.login(request));
    }

}