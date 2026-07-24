package com.monolith.modularmonolith.identity.internal.adapter.web;

import com.monolith.modularmonolith.identity.internal.application.port.inbound.*;
import com.monolith.modularmonolith.identity.internal.domain.model.ProfileType;
import com.monolith.modularmonolith.identity.internal.dto.request.*;
import com.monolith.modularmonolith.identity.internal.dto.response.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final CreateStudentUseCase createStudent;
    private final CreateTeacherUseCase createTeacher;
    private final CreateAdminUseCase createAdmin;
    private final GetUserProfileUseCase getUserProfile;
    private final ListUsersUseCase listUsers;
    private final PatchUserUseCase patchUser;
    private final DeactivateUserUseCase deactivateUser;
    private final ActivateUserUseCase activateUser;
    private final ResetPasswordUseCase resetPassword;
    private final DeleteUserUseCase deleteUser;
    private final GetUserStatisticsUseCase getStatistics;

    @PostMapping("/students")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<UserProfileResponse> createStudent(
            @Valid @RequestBody StudentCreateRequest request) {
        log.info("Creating student account: email={}", request.email());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createStudent.execute(request));
    }

    @PostMapping("/teachers")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<UserProfileResponse> createTeacher(
            @Valid @RequestBody TeacherCreateRequest request) {
        log.info("Creating teacher account: email={}", request.email());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createTeacher.execute(request));
    }

    @PostMapping("/admins")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<UserProfileResponse> createAdmin(
            @Valid @RequestBody AdminCreateRequest request) {
        log.info("Creating admin account: email={}", request.email());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createAdmin.execute(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Page<UserSummaryResponse>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) ProfileType profileType,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(listUsers.execute(pageable, profileType, search, active));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(getUserProfile.byId(userId));
    }

    @PatchMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<UserProfileResponse> patchUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserStatusUpdateRequest request) {
        log.info("Partial update user {} by admin", userId);
        return ResponseEntity.ok(patchUser.execute(userId, request));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long userId) {
        log.info("Deactivating account {} by admin", userId);
        deactivateUser.execute(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> activateUser(@PathVariable Long userId) {
        log.info("Reactivating account {} by admin", userId);
        activateUser.execute(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/reset-password")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PasswordResetResult> resetPassword(@PathVariable Long userId) {
        log.info("Resetting password for {}", userId);
        return ResponseEntity.ok(resetPassword.execute(userId));
    }

    @DeleteMapping("/{userId}/permanent")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteUserPermanently(@PathVariable Long userId) {
        log.warn("PERMANENT deletion of account {} by superadmin", userId);
        deleteUser.execute(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<UserStatistics> getStatistics() {
        return ResponseEntity.ok(getStatistics.execute());
    }
}