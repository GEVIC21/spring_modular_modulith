package com.monolith.modularmonolith.users.internal.controller;

import com.monolith.modularmonolith.users.internal.dto.ProfileResponse;
import com.monolith.modularmonolith.users.internal.dto.UpdateProfileRequest;
import com.monolith.modularmonolith.users.internal.dto.UploadAvatarResponse;
import com.monolith.modularmonolith.users.internal.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userProfileService.getProfileByEmail(userDetails.getUsername()));
    }

    @PutMapping("/me")
    public ResponseEntity<ProfileResponse> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userProfileService.updateProfile(userDetails.getUsername(), request));
    }

    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadAvatarResponse> uploadAvatar(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(userProfileService.updateAvatar(userDetails.getUsername(), file));
    }

    @DeleteMapping("/me/avatar")
    public ResponseEntity<Void> deleteAvatar(
            @AuthenticationPrincipal UserDetails userDetails) {
        userProfileService.deleteAvatar(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/me/avatar", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE, "image/svg+xml"})
    public ResponseEntity<Resource> getAvatarImage(
            @AuthenticationPrincipal UserDetails userDetails) {
        return userProfileService.loadAvatar(userDetails.getUsername());
    }
}