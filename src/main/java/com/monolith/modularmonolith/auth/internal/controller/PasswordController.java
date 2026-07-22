package com.monolith.modularmonolith.auth.internal.controller;

import com.monolith.modularmonolith.users.internal.dto.request.PasswordChangeRequest;
import com.monolith.modularmonolith.auth.internal.service.PasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users/password")
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordService passwordService;

    @PostMapping("/change")
    public ResponseEntity<Map<String, String>> changePassword(
            @Valid @RequestBody PasswordChangeRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        passwordService.changePassword(
                email,
                request.currentPassword(),
                request.newPassword(),
                request.confirmPassword()
        );

        return ResponseEntity.ok(Map.of("message", "Mot de passe changé avec succès. Veuillez vous reconnecter."));
    }
}