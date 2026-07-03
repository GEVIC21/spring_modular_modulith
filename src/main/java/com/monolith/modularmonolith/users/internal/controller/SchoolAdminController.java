package com.monolith.modularmonolith.users.internal.controller;

import com.monolith.modularmonolith.users.internal.dto.request.ActivateUserRequest;
import com.monolith.modularmonolith.users.internal.dto.request.RoleAssignmentRequest;
import com.monolith.modularmonolith.users.internal.dto.request.StudentRegisterRequest;
import com.monolith.modularmonolith.users.internal.dto.request.TeacherRegisterRequest;
import com.monolith.modularmonolith.users.internal.dto.response.StudentProfileResponse;
import com.monolith.modularmonolith.users.internal.dto.response.TeacherProfileResponse;
import com.monolith.modularmonolith.users.internal.dto.response.UserListResponse;
import com.monolith.modularmonolith.users.internal.service.SchoolUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class SchoolAdminController {

    private final SchoolUserService schoolUserService;

    // Inscription par l'admin
    @PostMapping("/students/register")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<StudentProfileResponse> registerStudent(@Valid @RequestBody StudentRegisterRequest request) {
        return ResponseEntity.ok(schoolUserService.registerStudent(request));
    }

    @PostMapping("/teachers/register")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<TeacherProfileResponse> registerTeacher(@Valid @RequestBody TeacherRegisterRequest request) {
        return ResponseEntity.ok(schoolUserService.registerTeacher(request));
    }

    // Gestion des utilisateurs
    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<List<UserListResponse>> listAllUsers() {
        return ResponseEntity.ok(schoolUserService.listAllUsers());
    }

    @GetMapping("/users/role/{roleName}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<List<UserListResponse>> listUsersByRole(@PathVariable String roleName) {
        return ResponseEntity.ok(schoolUserService.listUsersByRole(roleName));
    }

    @GetMapping("/students/{email}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'ENSEIGNANT')")
    public ResponseEntity<StudentProfileResponse> getStudent(@PathVariable String email) {
        return ResponseEntity.ok(schoolUserService.getStudentByEmail(email));
    }

    @GetMapping("/teachers/{email}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<TeacherProfileResponse> getTeacher(@PathVariable String email) {
        return ResponseEntity.ok(schoolUserService.getTeacherByEmail(email));
    }

    @PutMapping("/users/roles")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Map<String, String>> assignRoles(@Valid @RequestBody RoleAssignmentRequest request) {
        schoolUserService.assignRoles(request);
        return ResponseEntity.ok(Map.of("message", "Rôles mis à jour avec succès"));
    }

    @PutMapping("/users/active")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<Map<String, String>> toggleUserActive(@Valid @RequestBody ActivateUserRequest request) {
        schoolUserService.toggleUserActive(request.userId(), request.active());
        String status = request.active() ? "activé" : "désactivé";
        return ResponseEntity.ok(Map.of("message", "Utilisateur " + status));
    }

    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long userId) {
        schoolUserService.deleteUser(userId);
        return ResponseEntity.ok(Map.of("message", "Utilisateur supprimé avec succès"));
    }
}