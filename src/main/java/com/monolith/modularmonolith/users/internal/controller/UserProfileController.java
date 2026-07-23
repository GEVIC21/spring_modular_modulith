package com.monolith.modularmonolith.users.internal.controller;

import com.monolith.modularmonolith.users.internal.dto.request.UpdateAdminProfileRequest;
import com.monolith.modularmonolith.users.internal.dto.request.UpdateStudentProfileRequest;
import com.monolith.modularmonolith.users.internal.dto.request.UpdateTeacherProfileRequest;
import com.monolith.modularmonolith.users.internal.dto.response.AvatarUploadResponse;
import com.monolith.modularmonolith.users.internal.dto.response.MeResponse;
import com.monolith.modularmonolith.users.internal.service.AvatarService;
import com.monolith.modularmonolith.users.internal.service.SchoolUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

/**
 * Contrôleur de gestion du PROFIL CONNECTÉ.
 * Gère UNIQUEMENT les opérations sur le compte de l'utilisateur authentifié.
 *
 * Toute opération d'administration (création, liste, gestion d'autres users)
 * se trouve dans {@link UserAdminController}.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final SchoolUserService schoolUserService;
    private final AvatarService avatarService;

    // ==================== PROFIL CONNECTÉ (/me) ====================

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MeResponse> getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        log.info("Récupération du profil pour: {}", email);
        return ResponseEntity.ok(schoolUserService.getMyProfile(email));
    }

    @PutMapping("/me/student")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<MeResponse> updateMyStudentProfile(
            @Valid @RequestBody UpdateStudentProfileRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        log.info("Mise à jour profil élève pour: {}", email);
        return ResponseEntity.ok(schoolUserService.updateStudentProfile(email, request));
    }

    @PutMapping("/me/teacher")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<MeResponse> updateMyTeacherProfile(
            @Valid @RequestBody UpdateTeacherProfileRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        log.info("Mise à jour profil enseignant pour: {}", email);
        return ResponseEntity.ok(schoolUserService.updateTeacherProfile(email, request));
    }

    @PutMapping("/me/admin")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MeResponse> updateMyAdminProfile(
            @Valid @RequestBody UpdateAdminProfileRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        log.info("Mise à jour profil admin pour: {}", email);
        return ResponseEntity.ok(schoolUserService.updateAdminProfile(email, request));
    }

    // ==================== AVATAR PERSONNEL ====================

    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AvatarUploadResponse> uploadMyAvatar(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        String email = authentication.getName();
        log.info("Upload avatar pour: {}", email);

        String filename = avatarService.storeAvatar(file, email);
        schoolUserService.updateAvatar(email, filename);

        return ResponseEntity.ok(new AvatarUploadResponse(
                "Avatar mis à jour avec succès",
                filename,
                avatarService.getFileUrl(filename),
                avatarService.getThumbnailUrl(filename)
        ));
    }

    @DeleteMapping("/me/avatar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteMyAvatar(Authentication authentication) {
        String email = authentication.getName();
        log.info("Suppression avatar pour: {}", email);

        String oldFilename = schoolUserService.getAvatarFilename(email);
        schoolUserService.deleteAvatar(email);
        if (oldFilename != null) {
            avatarService.deleteFile(oldFilename);
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/me/avatar", produces = {
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE,
            MediaType.IMAGE_GIF_VALUE,
            "image/webp"
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource> getMyAvatarImage(Authentication authentication) {
        String email = authentication.getName();
        String filename = schoolUserService.getAvatarFilename(email);

        if (filename == null || filename.isBlank()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = avatarService.loadFileAsResource(filename);
        String contentType = avatarService.resolveContentType(filename);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
                .body(resource);
    }
}