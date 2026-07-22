package com.monolith.modularmonolith.users.internal.service;

import com.monolith.modularmonolith.users.internal.dto.request.*;
import com.monolith.modularmonolith.users.internal.dto.response.*;
import com.monolith.modularmonolith.users.internal.exception.UserNotFoundException;
import com.monolith.modularmonolith.users.internal.mapper.UserProfileMapper;
import com.monolith.modularmonolith.users.internal.model.*;
import com.monolith.modularmonolith.users.internal.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SchoolUserServiceImpl implements SchoolUserService {

    private final SchoolUserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final AdminProfileRepository adminProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileMapper userProfileMapper;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
    private static final int TEMP_PASSWORD_LENGTH = 12;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // ==================== PROFIL CONNECTÉ ====================

    @Override
    @Transactional(readOnly = true)
    public MeResponse getMyProfile(String email) {
        SchoolUser user = userRepository.findByEmailWithProfiles(email)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé: " + email));
        return userProfileMapper.toMeResponse(user);
    }

    @Override
    public MeResponse updateStudentProfile(String email, UpdateStudentProfileRequest request) {
        SchoolUser user = findUserByEmail(email);
        updateBaseUserInfo(user, request);
        updateStudentProfileInfo(user, request);
        user.setUpdatedAt(LocalDateTime.now());
        return userProfileMapper.toMeResponse(userRepository.save(user));
    }

    @Override
    public MeResponse updateTeacherProfile(String email, UpdateTeacherProfileRequest request) {
        SchoolUser user = findUserByEmail(email);
        updateBaseUserInfo(user, request);
        updateTeacherProfileInfo(user, request);
        user.setUpdatedAt(LocalDateTime.now());
        return userProfileMapper.toMeResponse(userRepository.save(user));
    }

    @Override
    public MeResponse updateAdminProfile(String email, UpdateAdminProfileRequest request) {
        SchoolUser user = findUserByEmail(email);
        updateBaseUserInfo(user, request);
        updateAdminProfileInfo(user, request);
        user.setUpdatedAt(LocalDateTime.now());
        return userProfileMapper.toMeResponse(userRepository.save(user));
    }

    // ==================== CRÉATION PAR ADMIN ====================

    @Override
    public MeResponse createStudent(StudentCreateRequest request) {
        validateEmailNotExists(request.email());

        String tempPassword = generateTemporaryPassword();

        SchoolUser user = SchoolUser.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(tempPassword))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phone(request.phone())
                .gender(request.gender())
                .birthDate(request.birthDate())
                .nationality(request.nationality())
                .language(request.language())
                .active(true)
                .emailVerified(false)
                .profileType("STUDENT")
                .roles(new HashSet<>(Set.of("STUDENT")))
                .build();

        StudentProfile student = StudentProfile.builder()
                .user(user)
                .studentId(generateStudentId())
                .gradeLevel(request.gradeLevel())
                .className(request.className())
                .section(request.section())
                .academicYear(request.academicYear())
                .enrollmentDate(request.enrollmentDate() != null ? request.enrollmentDate() : LocalDateTime.now().toLocalDate())
                .scholarship(request.scholarship())
                .scholarshipType(request.scholarshipType())
                .parentName(request.parentName())
                .parentEmail(request.parentEmail())
                .parentPhone(request.parentPhone())
                .emergencyContact(request.emergencyContact())
                .emergencyContactPhone(request.emergencyContactPhone())
                .address(request.address())
                .city(request.city())
                .postalCode(request.postalCode())
                .country(request.country())
                .bloodGroup(request.bloodGroup())
                .allergies(request.allergies())
                .medicalNotes(request.medicalNotes())
                .extracurricularActivities(request.extracurricularActivities() != null ? request.extracurricularActivities() : new HashSet<>())
                .build();

        user.setStudentProfile(student);

        SchoolUser saved = userRepository.save(user);
        log.info("Élève créé: id={}, studentId={}, email={}", saved.getId(), student.getStudentId(), saved.getEmail());

        // TODO: Envoyer email avec mot de passe temporaire
        // emailService.sendWelcomeEmail(saved.getEmail(), saved.getFirstName(), tempPassword);

        return userProfileMapper.toMeResponse(saved);
    }

    @Override
    public MeResponse createTeacher(TeacherCreateRequest request) {
        validateEmailNotExists(request.email());

        String tempPassword = generateTemporaryPassword();

        SchoolUser user = SchoolUser.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(tempPassword))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phone(request.phone())
                .gender(request.gender())
                .birthDate(request.birthDate())
                .nationality(request.nationality())
                .active(true)
                .emailVerified(false)
                .profileType("TEACHER")
                .roles(new HashSet<>(Set.of("TEACHER")))
                .build();

        TeacherProfile teacher = TeacherProfile.builder()
                .user(user)
                .teacherId(generateTeacherId())
                .employeeId(generateEmployeeId())
                .department(request.department())
                .specialization(request.specialization())
                .subjects(request.subjects() != null ? request.subjects() : new HashSet<>())
                .classesAssigned(request.classesAssigned() != null ? request.classesAssigned() : new HashSet<>())
                .hireDate(request.hireDate())
                .contractEndDate(request.contractEndDate())
                .contractType(request.contractType())
                .qualification(request.qualification())
                .certifications(request.certifications())
                .officeLocation(request.officeLocation())
                .officeHours(request.officeHours())
                .bio(request.bio())
                .researchInterests(request.researchInterests())
                .yearsOfExperience(request.yearsOfExperience())
                .departmentHead(request.departmentHead())
                .build();

        user.setTeacherProfile(teacher);

        SchoolUser saved = userRepository.save(user);
        log.info("Enseignant créé: id={}, teacherId={}, email={}", saved.getId(), teacher.getTeacherId(), saved.getEmail());

        return userProfileMapper.toMeResponse(saved);
    }

    @Override
    public MeResponse createAdmin(AdminCreateRequest request) {
        validateEmailNotExists(request.email());

        String tempPassword = generateTemporaryPassword();

        SchoolUser user = SchoolUser.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(tempPassword))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phone(request.phone())
                .active(true)
                .emailVerified(true)
                .profileType("ADMIN")
                .roles(new HashSet<>(Set.of("ADMIN")))
                .build();

        AdminProfile admin = AdminProfile.builder()
                .user(user)
                .adminId(generateAdminId())
                .department(request.department())
                .jobTitle(request.jobTitle())
                .hireDate(request.hireDate())
                .accessLevel(request.accessLevel())
                .managedModules(request.managedModules() != null ? request.managedModules() : new HashSet<>())
                .canManageUsers(request.canManageUsers())
                .canManageFinances(request.canManageFinances())
                .canManageAcademics(request.canManageAcademics())
                .officePhone(request.officePhone())
                .officeLocation(request.officeLocation())
                .build();

        user.setAdminProfile(admin);

        SchoolUser saved = userRepository.save(user);
        log.info("Admin créé: id={}, adminId={}, email={}", saved.getId(), admin.getAdminId(), saved.getEmail());

        return userProfileMapper.toMeResponse(saved);
    }

    @Override
    public BulkCreationResponse createStudentsBulk(MultipartFile file) {
        log.info("Import bulk d'élèves");
        // TODO: Implémenter parsing CSV/Excel avec Apache POI ou OpenCSV
        throw new UnsupportedOperationException("Import bulk non encore implémenté");
    }

    // ==================== LECTURE ====================

    @Override
    @Transactional(readOnly = true)
    public MeResponse getProfileById(Long userId) {
        SchoolUser user = userRepository.findByIdWithProfiles(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé: " + userId));
        return userProfileMapper.toMeResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserProfileSummaryResponse> listUsers(int page, int size, String profileType, String search, Boolean active) {
        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return userRepository.findAllWithFilters(profileType, search, active, pageable)
                .map(userProfileMapper::toUserProfileSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserProfileSummaryResponse> searchUsers(String firstName, String lastName, String email,
                                                        String studentId, String teacherId, String gradeLevel, String department) {
        // TODO: Implémenter recherche avancée avec criteria/specifications
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public UserStatsResponse getUserStats() {
        long total = userRepository.count();
        long students = userRepository.countByProfileType("STUDENT");
        long teachers = userRepository.countByProfileType("TEACHER");
        long admins = userRepository.countByProfileType("ADMIN");
        long active = userRepository.countByActive(true);
        long inactive = userRepository.countByActive(false);

        return new UserStatsResponse(total, students, teachers, admins, active, inactive, 0, 0);
    }

    // ==================== MISE À JOUR PAR ADMIN ====================

    @Override
    public MeResponse updateStudentByAdmin(Long userId, StudentCreateRequest request) {
        SchoolUser user = findUserById(userId);
        // TODO: Implémenter mise à jour complète
        return userProfileMapper.toMeResponse(user);
    }

    @Override
    public MeResponse updateTeacherByAdmin(Long userId, TeacherCreateRequest request) {
        SchoolUser user = findUserById(userId);
        // TODO: Implémenter mise à jour complète
        return userProfileMapper.toMeResponse(user);
    }

    @Override
    public MeResponse patchUser(Long userId, UserPatchRequest request) {
        SchoolUser user = findUserById(userId);

        if (request.active() != null) user.setActive(request.active());
        if (request.profileType() != null) user.setProfileType(request.profileType());
        if (request.roles() != null) user.setRoles(request.roles());
        if (request.emailVerified() != null) user.setEmailVerified(request.emailVerified());

        user.setUpdatedAt(LocalDateTime.now());
        return userProfileMapper.toMeResponse(userRepository.save(user));
    }

    // ==================== GESTION DES COMPTES ====================

    @Override
    public void deactivateUser(Long userId) {
        SchoolUser user = findUserById(userId);
        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        log.info("Utilisateur désactivé: {}", userId);
    }

    @Override
    public void activateUser(Long userId) {
        SchoolUser user = findUserById(userId);
        user.setActive(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        log.info("Utilisateur réactivé: {}", userId);
    }

    @Override
    public PasswordResetResponse resetPassword(Long userId) {
        SchoolUser user = findUserById(userId);
        String tempPassword = generateTemporaryPassword();
        user.setPassword(passwordEncoder.encode(tempPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("Mot de passe réinitialisé pour: {}", userId);

        // TODO: Envoyer email avec nouveau mot de passe
        return new PasswordResetResponse(
                "Mot de passe réinitialisé avec succès",
                false, // emailSent
                tempPassword // À afficher à l'admin si email échoue
        );
    }

    @Override
    public void deleteUserPermanently(Long userId) {
        userRepository.deleteById(userId);
        log.warn("Utilisateur supprimé PERMANENTEMENT: {}", userId);
    }

    // ==================== AVATAR ====================

    @Override
    public void updateAvatar(String email, String filename) {
        SchoolUser user = findUserByEmail(email);
        user.setAvatarUrl(filename);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public void deleteAvatar(String email) {
        SchoolUser user = findUserByEmail(email);
        user.setAvatarUrl(null);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public String getAvatarFilename(String email) {
        return userRepository.findByEmail(email)
                .map(SchoolUser::getAvatarUrl)
                .orElse(null);
    }

    // ==================== MÉTHODES PRIVÉES ====================

    private SchoolUser findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé: " + email));
    }

    private SchoolUser findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé: " + userId));
    }

    private void validateEmailNotExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Un utilisateur avec l'email " + email + " existe déjà");
        }
    }

    private String generateTemporaryPassword() {
        StringBuilder sb = new StringBuilder(TEMP_PASSWORD_LENGTH);
        for (int i = 0; i < TEMP_PASSWORD_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(SECURE_RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    private String generateStudentId() {
        String year = String.valueOf(LocalDateTime.now().getYear());
        long count = studentProfileRepository.count() + 1;
        return String.format("STD-%s-%05d", year, count);
    }

    private String generateTeacherId() {
        String year = String.valueOf(LocalDateTime.now().getYear());
        long count = teacherProfileRepository.count() + 1;
        return String.format("TCH-%s-%05d", year, count);
    }

    private String generateEmployeeId() {
        long count = teacherProfileRepository.count() + adminProfileRepository.count() + 1;
        return String.format("EMP-%05d", count);
    }

    private String generateAdminId() {
        long count = adminProfileRepository.count() + 1;
        return String.format("ADM-%05d", count);
    }

    // Méthodes utilitaires pour mise à jour
    private void updateBaseUserInfo(SchoolUser user, Object request) {
        // Extraction par réflexion ou casting selon le type
        // Simplifié ici — à adapter selon tes besoins
    }

    private void updateStudentProfileInfo(SchoolUser user, UpdateStudentProfileRequest request) {
        StudentProfile profile = user.getStudentProfile();
        if (profile == null) {
            profile = new StudentProfile();
            profile.setUser(user);
            user.setStudentProfile(profile);
        }
        if (request.parentName() != null) profile.setParentName(request.parentName());
        if (request.parentEmail() != null) profile.setParentEmail(request.parentEmail());
        if (request.parentPhone() != null) profile.setParentPhone(request.parentPhone());
        if (request.emergencyContact() != null) profile.setEmergencyContact(request.emergencyContact());
        if (request.emergencyContactPhone() != null) profile.setEmergencyContactPhone(request.emergencyContactPhone());
        if (request.address() != null) profile.setAddress(request.address());
        if (request.city() != null) profile.setCity(request.city());
        if (request.postalCode() != null) profile.setPostalCode(request.postalCode());
        if (request.country() != null) profile.setCountry(request.country());
        if (request.bloodGroup() != null) profile.setBloodGroup(request.bloodGroup());
        if (request.allergies() != null) profile.setAllergies(request.allergies());
        if (request.medicalNotes() != null) profile.setMedicalNotes(request.medicalNotes());
        if (request.extracurricularActivities() != null) profile.setExtracurricularActivities(request.extracurricularActivities());
    }

    private void updateTeacherProfileInfo(SchoolUser user, UpdateTeacherProfileRequest request) {
        // TODO: Implémenter
    }

    private void updateAdminProfileInfo(SchoolUser user, UpdateAdminProfileRequest request) {
        // TODO: Implémenter
    }
}