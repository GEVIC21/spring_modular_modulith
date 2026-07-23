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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
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
    @Transactional
    public MeResponse updateStudentProfile(String email, UpdateStudentProfileRequest request) {
        SchoolUser user = findUserByEmail(email);
        updateBaseUserInfo(user, request);
        updateStudentProfileInfo(user, request);
        user.setUpdatedAt(LocalDateTime.now());
        return userProfileMapper.toMeResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public MeResponse updateTeacherProfile(String email, UpdateTeacherProfileRequest request) {
        SchoolUser user = findUserByEmail(email);
        updateBaseUserInfo(user, request);
        updateTeacherProfileInfo(user, request);
        user.setUpdatedAt(LocalDateTime.now());
        return userProfileMapper.toMeResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public MeResponse updateAdminProfile(String email, UpdateAdminProfileRequest request) {
        SchoolUser user = findUserByEmail(email);
        updateBaseUserInfo(user, request);
        updateAdminProfileInfo(user, request);
        user.setUpdatedAt(LocalDateTime.now());
        return userProfileMapper.toMeResponse(userRepository.save(user));
    }

    // ==================== CRÉATION PAR ADMIN ====================

    @Override
    @Transactional
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
                .enrollmentDate(request.enrollmentDate() != null ? request.enrollmentDate() : LocalDate.now())
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
                .extracurricularActivities(request.extracurricularActivities() != null
                        ? new HashSet<>(request.extracurricularActivities()) : new HashSet<>())
                .build();

        user.setStudentProfile(student);

        SchoolUser saved = userRepository.save(user);
        log.info("Élève créé: id={}, studentId={}, email={}", saved.getId(), student.getStudentId(), saved.getEmail());

        // TODO: Envoyer email avec mot de passe temporaire
        return userProfileMapper.toMeResponse(saved);
    }

    @Override
    @Transactional
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
                .subjects(request.subjects() != null ? new HashSet<>(request.subjects()) : new HashSet<>())
                .classesAssigned(request.classesAssigned() != null ? new HashSet<>(request.classesAssigned()) : new HashSet<>())
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
    @Transactional
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
                .managedModules(request.managedModules() != null ? new HashSet<>(request.managedModules()) : new HashSet<>())
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
    @Transactional
    public BulkCreationResponse createStudentsBulk(MultipartFile file) {
        log.info("Import bulk d'élèves");
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
    @Transactional
    public MeResponse updateStudentByAdmin(Long userId, StudentCreateRequest request) {
        SchoolUser user = findUserById(userId);
        return userProfileMapper.toMeResponse(user);
    }

    @Override
    @Transactional
    public MeResponse updateTeacherByAdmin(Long userId, TeacherCreateRequest request) {
        SchoolUser user = findUserById(userId);
        return userProfileMapper.toMeResponse(user);
    }

    @Override
    @Transactional
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
    @Transactional
    public void deactivateUser(Long userId) {
        SchoolUser user = findUserById(userId);
        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        log.info("Utilisateur désactivé: {}", userId);
    }

    @Override
    @Transactional
    public void activateUser(Long userId) {
        SchoolUser user = findUserById(userId);
        user.setActive(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        log.info("Utilisateur réactivé: {}", userId);
    }

    @Override
    @Transactional
    public PasswordResetResponse resetPassword(Long userId) {
        SchoolUser user = findUserById(userId);
        String tempPassword = generateTemporaryPassword();
        user.setPassword(passwordEncoder.encode(tempPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("Mot de passe réinitialisé pour: {}", userId);

        return new PasswordResetResponse(
                "Mot de passe réinitialisé avec succès",
                false,
                tempPassword
        );
    }

    @Override
    @Transactional
    public void deleteUserPermanently(Long userId) {
        userRepository.deleteById(userId);
        log.warn("Utilisateur supprimé PERMANENTEMENT: {}", userId);
    }

    // ==================== AVATAR ====================

    @Override
    @Transactional
    public void updateAvatar(String email, String filename) {
        SchoolUser user = findUserByEmail(email);
        user.setAvatarUrl(filename);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    @Transactional
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

    // ==================== MISE À JOUR BASE USER ====================

    private void updateBaseUserInfo(SchoolUser user, Object request) {
        if (request instanceof UpdateStudentProfileRequest r) {
            if (r.firstName() != null) user.setFirstName(r.firstName());
            if (r.lastName() != null) user.setLastName(r.lastName());
            if (r.phone() != null) user.setPhone(r.phone());
            if (r.gender() != null) user.setGender(r.gender());
            if (r.birthDate() != null) user.setBirthDate(r.birthDate());
            if (r.nationality() != null) user.setNationality(r.nationality());
            if (r.language() != null) user.setLanguage(r.language());
            if (r.timezone() != null) user.setTimezone(r.timezone());
        } else if (request instanceof UpdateTeacherProfileRequest r) {
            if (r.firstName() != null) user.setFirstName(r.firstName());
            if (r.lastName() != null) user.setLastName(r.lastName());
            if (r.phone() != null) user.setPhone(r.phone());
            if (r.gender() != null) user.setGender(r.gender());
            if (r.birthDate() != null) user.setBirthDate(r.birthDate());
            if (r.nationality() != null) user.setNationality(r.nationality());
            if (r.language() != null) user.setLanguage(r.language());
            if (r.timezone() != null) user.setTimezone(r.timezone());
        } else if (request instanceof UpdateAdminProfileRequest r) {
            if (r.firstName() != null) user.setFirstName(r.firstName());
            if (r.lastName() != null) user.setLastName(r.lastName());
            if (r.phone() != null) user.setPhone(r.phone());
        }
    }

    // ==================== MISE À JOUR PROFILS SPÉCIFIQUES ====================

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
        if (request.extracurricularActivities() != null)
            profile.setExtracurricularActivities(new HashSet<>(request.extracurricularActivities()));
    }

    private void updateTeacherProfileInfo(SchoolUser user, UpdateTeacherProfileRequest request) {
        TeacherProfile profile = user.getTeacherProfile();
        if (profile == null) {
            profile = new TeacherProfile();
            profile.setUser(user);
            user.setTeacherProfile(profile);
        }
        if (request.department() != null) profile.setDepartment(request.department());
        if (request.specialization() != null) profile.setSpecialization(request.specialization());
        if (request.subjects() != null) profile.setSubjects(new HashSet<>(request.subjects()));
        if (request.classesAssigned() != null) profile.setClassesAssigned(new HashSet<>(request.classesAssigned()));
        if (request.qualification() != null) profile.setQualification(request.qualification());
        if (request.certifications() != null) profile.setCertifications(request.certifications());
        if (request.officeLocation() != null) profile.setOfficeLocation(request.officeLocation());
        if (request.officeHours() != null) profile.setOfficeHours(request.officeHours());
        if (request.bio() != null) profile.setBio(request.bio());
        if (request.researchInterests() != null) profile.setResearchInterests(request.researchInterests());
        if (request.contractType() != null) profile.setContractType(request.contractType());
        if (request.yearsOfExperience() != null) profile.setYearsOfExperience(request.yearsOfExperience());
        if (request.departmentHead() != null) profile.setDepartmentHead(request.departmentHead());
    }

    private void updateAdminProfileInfo(SchoolUser user, UpdateAdminProfileRequest request) {
        AdminProfile profile = user.getAdminProfile();
        if (profile == null) {
            profile = new AdminProfile();
            profile.setUser(user);
            user.setAdminProfile(profile);
        }
        if (request.department() != null) profile.setDepartment(request.department());
        if (request.jobTitle() != null) profile.setJobTitle(request.jobTitle());
        if (request.accessLevel() != null) profile.setAccessLevel(request.accessLevel());
        if (request.managedModules() != null) profile.setManagedModules(new HashSet<>(request.managedModules()));
        if (request.canManageUsers() != null) profile.setCanManageUsers(request.canManageUsers());
        if (request.canManageFinances() != null) profile.setCanManageFinances(request.canManageFinances());
        if (request.canManageAcademics() != null) profile.setCanManageAcademics(request.canManageAcademics());
        if (request.officePhone() != null) profile.setOfficePhone(request.officePhone());
        if (request.officeLocation() != null) profile.setOfficeLocation(request.officeLocation());
    }
}