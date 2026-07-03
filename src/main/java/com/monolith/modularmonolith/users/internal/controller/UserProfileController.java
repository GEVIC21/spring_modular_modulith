package com.monolith.modularmonolith.users.internal.controller;

import com.monolith.modularmonolith.users.internal.dto.response.AvatarUploadResponse;
import com.monolith.modularmonolith.users.internal.service.AvatarService;
import com.monolith.modularmonolith.users.internal.service.SchoolUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final SchoolUserService schoolUserService;
    private final AvatarService avatarService;

    /**
     * Retourne le profil complet de l'utilisateur connecté
     * (StudentProfileResponse si élève, TeacherProfileResponse si enseignant,
     *  UserListResponse si admin/superadmin)
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(schoolUserService.getMyProfile(email));
    }

    /**
     * Upload de l'avatar personnel
     */
    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AvatarUploadResponse> uploadMyAvatar(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        String email = authentication.getName();
        String filename = avatarService.storeFile(file, email);
        schoolUserService.updateAvatar(email, filename);

        return ResponseEntity.ok(new AvatarUploadResponse(
                "Avatar mis à jour avec succès",
                filename,
                avatarService.getFileUrl(filename)
        ));
    }

    /**
     * Suppression de l'avatar personnel
     */
    @DeleteMapping("/me/avatar")
    public ResponseEntity<Void> deleteMyAvatar(Authentication authentication) {
        String email = authentication.getName();
        schoolUserService.deleteAvatar(email);
        return ResponseEntity.noContent().build();
    }

    /**
     * Récupération directe de l'image avatar (affichage navigateur)
     */
    @GetMapping(value = "/me/avatar", produces = {
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE,
            MediaType.IMAGE_GIF_VALUE,
            "image/webp"
    })
    public ResponseEntity<Resource> getMyAvatarImage(Authentication authentication) {
        String email = authentication.getName();
        String filename = schoolUserService.getAvatarFilename(email);

        Resource resource = avatarService.loadFileAsResource(filename);

        // Détection du content-type selon l'extension
        String contentType = MediaType.IMAGE_JPEG_VALUE;
        if (filename.endsWith(".png")) contentType = MediaType.IMAGE_PNG_VALUE;
        else if (filename.endsWith(".gif")) contentType = MediaType.IMAGE_GIF_VALUE;
        else if (filename.endsWith(".webp")) contentType = "image/webp";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
}