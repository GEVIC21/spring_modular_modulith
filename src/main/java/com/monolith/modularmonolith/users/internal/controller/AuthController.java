package com.monolith.modularmonolith.users.internal.controller;

import com.monolith.modularmonolith.security.jwt.JwtUtils;
import com.monolith.modularmonolith.users.internal.dto.AuthResponse;
import com.monolith.modularmonolith.users.internal.dto.LoginRequest;
import com.monolith.modularmonolith.users.internal.dto.RegisterRequest;
import com.monolith.modularmonolith.users.internal.model.User;
import com.monolith.modularmonolith.users.internal.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User registeredUser = userService.registerUser(request.username(), request.email(), request.password());
        String token = jwtUtils.generateToken(registeredUser);

        // Utilisation de getPublicUsername() pour passer le 3ème argument requis
        AuthResponse response = new AuthResponse(
                token,
                registeredUser.getEmail(),
                registeredUser.getPublicUsername()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User userDetails = (User) authentication.getPrincipal();
        String token = jwtUtils.generateToken(userDetails);

        // Correction de l'erreur "Expected 2 arguments but found 3"
        AuthResponse response = new AuthResponse(
                token,
                userDetails.getEmail(),
                userDetails.getPublicUsername()
        );

        return ResponseEntity.ok(response);
    }
}
