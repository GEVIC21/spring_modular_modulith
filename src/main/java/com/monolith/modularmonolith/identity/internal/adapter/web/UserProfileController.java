package com.monolith.modularmonolith.identity.internal.adapter.web;

import com.monolith.modularmonolith.identity.internal.application.port.inbound.*;
import com.monolith.modularmonolith.identity.internal.dto.request.*;
import com.monolith.modularmonolith.identity.internal.dto.response.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final GetUserProfileUseCase getUserProfile;
    private final UpdateStudentProfileUseCase updateStudentProfile;
    private final UpdateTeacherProfileUseCase updateTeacherProfile;
    private final UpdateAdminProfileUseCase updateAdminProfile;
    private final UpdateAvatarUseCase updateAvatar;
    private final GetAvatarUseCase getAvatar;
    private final com.monolith.modularmonolith.identity.internal.application.port.outbound.FileStorage fileStorage;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileResponse> getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        log.info("Getting profile for: {}", email);
        return ResponseEntity.ok(getUserProfile.byEmail(email));
    }

    @PutMapping("/me/student")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<UserProfileResponse> updateMyStudentProfile(
            @Valid @RequestBody UpdateStudentProfileRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        log.info("Updating student profile for: {}", email);
        return ResponseEntity.ok(updateStudentProfile.execute(email, request));
    }

    @PutMapping("/me/teacher")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<UserProfileResponse> updateMyTeacherProfile(
            @Valid @RequestBody UpdateTeacherProfileRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        log.info("Updating teacher profile for: {}", email);
        return ResponseEntity.ok(updateTeacherProfile.execute(email, request));
    }

    @PutMapping("/me/admin")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<UserProfileResponse> updateMyAdminProfile(
            @Valid @RequestBody UpdateAdminProfileRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        log.info("Updating admin profile for: {}", email);
        return ResponseEntity.ok(updateAdminProfile.execute(email, request));
    }

    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AvatarUploadResult> uploadMyAvatar(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        String email = authentication.getName();
        log.info("Uploading avatar for: {}", email);

        String filename = fileStorage.store(file, email);
        updateAvatar.execute(email, filename);

        return ResponseEntity.ok(new AvatarUploadResult(
                "Avatar mis à jour avec succès",
                filename,
                fileStorage.getFileUrl(filename),
                fileStorage.getThumbnailUrl(filename)
        ));
    }

    @DeleteMapping("/me/avatar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteMyAvatar(Authentication authentication) {
        String email = authentication.getName();
        log.info("Deleting avatar for: {}", email);

        String oldFilename = getAvatar.getFilename(email);
        getAvatar.delete(email);
        if (oldFilename != null) {
            fileStorage.delete(oldFilename);
        }

        return ResponseEntity.noContent().build();
    }
}