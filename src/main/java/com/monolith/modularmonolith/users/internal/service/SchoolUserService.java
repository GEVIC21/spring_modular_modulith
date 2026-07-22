package com.monolith.modularmonolith.users.internal.service;

import com.monolith.modularmonolith.users.internal.dto.request.*;
import com.monolith.modularmonolith.users.internal.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SchoolUserService {

    // ========== PROFIL CONNECTÉ (/me) ==========
    MeResponse getMyProfile(String email);

    MeResponse updateStudentProfile(String email, UpdateStudentProfileRequest request);

    MeResponse updateTeacherProfile(String email, UpdateTeacherProfileRequest request);

    MeResponse updateAdminProfile(String email, UpdateAdminProfileRequest request);

    // ========== CRÉATION PAR ADMIN ==========
    MeResponse createStudent(StudentCreateRequest request);

    MeResponse createTeacher(TeacherCreateRequest request);

    MeResponse createAdmin(AdminCreateRequest request);

    BulkCreationResponse createStudentsBulk(MultipartFile file);

    // ========== LECTURE ==========
    MeResponse getProfileById(Long userId);

    Page<UserProfileSummaryResponse> listUsers(int page, int size, String profileType, String search, Boolean active);

    List<UserProfileSummaryResponse> searchUsers(String firstName, String lastName, String email,
                                                 String studentId, String teacherId, String gradeLevel, String department);

    UserStatsResponse getUserStats();

    // ========== MISE À JOUR PAR ADMIN ==========
    MeResponse updateStudentByAdmin(Long userId, StudentCreateRequest request);

    MeResponse updateTeacherByAdmin(Long userId, TeacherCreateRequest request);

    MeResponse patchUser(Long userId, UserPatchRequest request);

    // ========== GESTION DES COMPTES ==========
    void deactivateUser(Long userId);

    void activateUser(Long userId);

    PasswordResetResponse resetPassword(Long userId);

    void deleteUserPermanently(Long userId);

    // ========== AVATAR ==========
    void updateAvatar(String email, String filename);

    void deleteAvatar(String email);

    String getAvatarFilename(String email);
}