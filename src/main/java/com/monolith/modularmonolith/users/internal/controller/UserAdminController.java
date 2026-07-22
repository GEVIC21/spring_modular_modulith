package com.monolith.modularmonolith.users.internal.controller;

import com.monolith.modularmonolith.users.internal.dto.request.AdminCreateRequest;
import com.monolith.modularmonolith.users.internal.dto.request.StudentCreateRequest;
import com.monolith.modularmonolith.users.internal.dto.request.TeacherCreateRequest;
import com.monolith.modularmonolith.users.internal.dto.request.UserPatchRequest;
import com.monolith.modularmonolith.users.internal.dto.response.BulkCreationResponse;
import com.monolith.modularmonolith.users.internal.dto.response.MeResponse;
import com.monolith.modularmonolith.users.internal.dto.response.PasswordResetResponse;
import com.monolith.modularmonolith.users.internal.dto.response.UserProfileSummaryResponse;
import com.monolith.modularmonolith.users.internal.dto.response.UserStatsResponse;
import com.monolith.modularmonolith.users.internal.service.SchoolUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Contrôleur d'ADMINISTRATION des utilisateurs.
 * Seuls les ADMIN et SUPER_ADMIN peuvent accéder à ces endpoints.
 *
 * Pour le profil connecté, voir {@link UserProfileController}.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final SchoolUserService schoolUserService;

    // ==================== CRÉATION DE COMPTES ====================

    @PostMapping("/students")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MeResponse> createStudent(
            @Valid @RequestBody StudentCreateRequest request) {
        log.info("Création compte élève: email={}, grade={}", request.email(), request.gradeLevel());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(schoolUserService.createStudent(request));
    }

    @PostMapping("/teachers")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MeResponse> createTeacher(
            @Valid @RequestBody TeacherCreateRequest request) {
        log.info("Création compte enseignant: email={}, dept={}", request.email(), request.department());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(schoolUserService.createTeacher(request));
    }

    @PostMapping("/admins")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<MeResponse> createAdmin(
            @Valid @RequestBody AdminCreateRequest request) {
        log.info("Création compte admin par superadmin: email={}", request.email());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(schoolUserService.createAdmin(request));
    }

    @PostMapping("/students/bulk")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<BulkCreationResponse> createStudentsBulk(
            @RequestParam("file") MultipartFile file) {
        log.info("Import massif d'élèves");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(schoolUserService.createStudentsBulk(file));
    }

    // ==================== LECTURE ====================

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Page<UserProfileSummaryResponse>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String profileType,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(schoolUserService.listUsers(page, size, profileType, search, active));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MeResponse> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(schoolUserService.getProfileById(userId));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<UserProfileSummaryResponse>> searchUsers(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) String teacherId,
            @RequestParam(required = false) String gradeLevel,
            @RequestParam(required = false) String department) {
        return ResponseEntity.ok(schoolUserService.searchUsers(
                firstName, lastName, email, studentId, teacherId, gradeLevel, department));
    }

    // ==================== MISE À JOUR ====================

    @PutMapping("/students/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MeResponse> updateStudent(
            @PathVariable Long userId,
            @Valid @RequestBody StudentCreateRequest request) {
        log.info("Mise à jour élève {} par admin", userId);
        return ResponseEntity.ok(schoolUserService.updateStudentByAdmin(userId, request));
    }

    @PutMapping("/teachers/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MeResponse> updateTeacher(
            @PathVariable Long userId,
            @Valid @RequestBody TeacherCreateRequest request) {
        log.info("Mise à jour enseignant {} par admin", userId);
        return ResponseEntity.ok(schoolUserService.updateTeacherByAdmin(userId, request));
    }

    @PatchMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MeResponse> patchUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserPatchRequest request) {
        log.info("Mise à jour partielle utilisateur {} par admin", userId);
        return ResponseEntity.ok(schoolUserService.patchUser(userId, request));
    }

    // ==================== GESTION DES COMPTES ====================

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long userId) {
        log.info("Désactivation compte {} par admin", userId);
        schoolUserService.deactivateUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> activateUser(@PathVariable Long userId) {
        log.info("Réactivation compte {} par admin", userId);
        schoolUserService.activateUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/reset-password")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PasswordResetResponse> resetPassword(@PathVariable Long userId) {
        log.info("Réinitialisation mot de passe pour {}", userId);
        return ResponseEntity.ok(schoolUserService.resetPassword(userId));
    }

    @DeleteMapping("/{userId}/permanent")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteUserPermanently(@PathVariable Long userId) {
        log.warn("Suppression PERMANENTE compte {} par superadmin", userId);
        schoolUserService.deleteUserPermanently(userId);
        return ResponseEntity.noContent().build();
    }

    // ==================== STATISTIQUES ====================

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<UserStatsResponse> getUserStats() {
        return ResponseEntity.ok(schoolUserService.getUserStats());
    }
}