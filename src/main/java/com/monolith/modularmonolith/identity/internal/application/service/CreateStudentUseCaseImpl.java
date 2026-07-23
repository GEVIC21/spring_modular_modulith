package com.monolith.modularmonolith.identity.internal.application.service;

import com.monolith.modularmonolith.identity.api.events.UserCreatedEvent;
import com.monolith.modularmonolith.identity.internal.application.port.inbound.CreateStudentUseCase;
import com.monolith.modularmonolith.identity.internal.application.port.outbound.EventPublisher;
import com.monolith.modularmonolith.identity.internal.domain.model.*;
import com.monolith.modularmonolith.identity.internal.domain.repository.StudentProfileRepository;
import com.monolith.modularmonolith.identity.internal.domain.repository.UserRepository;
import com.monolith.modularmonolith.identity.internal.dto.request.StudentCreateRequest;
import com.monolith.modularmonolith.identity.internal.dto.response.UserProfileResponse;
import com.monolith.modularmonolith.identity.internal.mapper.UserProfileMapper;
import com.monolith.modularmonolith.shared.exception.ConflictException;
import com.monolith.modularmonolith.shared.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateStudentUseCaseImpl implements CreateStudentUseCase {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileMapper userProfileMapper;
    private final EventPublisher eventPublisher;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
    private static final int TEMP_PASSWORD_LENGTH = 12;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    @Transactional
    public UserProfileResponse execute(StudentCreateRequest request) {
        validateEmailNotExists(request.email());
        validateUsernameNotExists(request.username());

        String tempPassword = generateTemporaryPassword();

        User user = User.builder()
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
                .profileType(ProfileType.STUDENT)
                .roles(new HashSet<>(Set.of(Role.STUDENT)))
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

        User saved = userRepository.save(user);
        log.info("Student created: id={}, studentId={}, email={}", saved.getId(), student.getStudentId(), saved.getEmail());

        eventPublisher.publish(new UserCreatedEvent(
                saved.getId(), saved.getEmail(), saved.getUsername(),
                Set.of(Role.STUDENT.name()), ProfileType.STUDENT.name(), null
        ));

        return userProfileMapper.toUserProfileResponse(saved);
    }

    private void validateEmailNotExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException(ErrorCode.USER_ALREADY_EXISTS);
        }
    }

    private void validateUsernameNotExists(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new ConflictException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
    }

    private String generateTemporaryPassword() {
        StringBuilder sb = new StringBuilder(TEMP_PASSWORD_LENGTH);
        for (int i = 0; i < TEMP_PASSWORD_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    private String generateStudentId() {
        String year = String.valueOf(LocalDateTime.now().getYear());
        long count = studentProfileRepository.count() + 1;
        return String.format("STD-%s-%05d", year, count);
    }
}