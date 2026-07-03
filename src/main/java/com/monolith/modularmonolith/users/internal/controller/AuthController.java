package com.monolith.modularmonolith.users.internal.controller;

import com.monolith.modularmonolith.security.jwt.JwtUtils;
import com.monolith.modularmonolith.users.internal.dto.response.AuthResponse;
import com.monolith.modularmonolith.users.internal.dto.request.LoginRequest;
import com.monolith.modularmonolith.users.internal.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtUtils.generateToken(user);

        return ResponseEntity.ok(new AuthResponse(
                token,
                user.getUsername(),      // email (car getUsername() retourne email)
                user.getPublicUsername() // pseudo public
        ));
    }
}