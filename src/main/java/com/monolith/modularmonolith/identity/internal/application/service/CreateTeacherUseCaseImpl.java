package com.monolith.modularmonolith.identity.internal.application.service;

import com.monolith.modularmonolith.identity.api.events.UserCreatedEvent;
import com.monolith.modularmonolith.identity.internal.application.port.inbound.CreateTeacherUseCase;
import com.monolith.modularmonolith.identity.internal.application.port.outbound.EventPublisher;
import com.monolith.modularmonolith.identity.internal.domain.model.ProfileType;
import com.monolith.modularmonolith.identity.internal.domain.model.Role;
import com.monolith.modularmonolith.identity.internal.domain.model.TeacherProfile;
import com.monolith.modularmonolith.identity.internal.domain.model.User;
import com.monolith.modularmonolith.identity.internal.domain.repository.TeacherProfileRepository;
import com.monolith.modularmonolith.identity.internal.domain.repository.UserRepository;
import com.monolith.modularmonolith.identity.internal.dto.request.TeacherCreateRequest;
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
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateTeacherUseCaseImpl implements CreateTeacherUseCase {

    private final UserRepository userRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileMapper userProfileMapper;
    private final EventPublisher eventPublisher;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
    private static final int TEMP_PASSWORD_LENGTH = 12;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    @Transactional
    public UserProfileResponse execute(TeacherCreateRequest request) {
        validateEmailNotExists(request.email());
        validateUsernameNotExists(request.username());

        if (teacherProfileRepository.existsByEmployeeId(request.employeeId())) {
            throw new ConflictException(ErrorCode.IDENTITY_005, "ID employé déjà existant");
        }

        String tempPassword = generateTemporaryPassword();

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(tempPassword))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phoneNumber(request.phoneNumber())
                .active(true)
                .emailVerified(false)
                .profileType(ProfileType.TEACHER)
                .roles(new HashSet<>(request.roles() != null ? request.roles() : Set.of(Role.TEACHER)))
                .build();

        TeacherProfile teacher = TeacherProfile.builder()
                .user(user)
                .teacherId(generateTeacherId())
                .employeeId(request.employeeId())
                .department(request.department())
                .subjects(request.subjects())
                .classesAssigned(request.classesAssigned())
                .qualification(request.qualification())
                .hireDate(request.hireDate() != null ? request.hireDate() : LocalDate.now())
                .specialization(request.specialization())
                .officeLocation(request.officeLocation())
                .officeHours(request.officeHours())
                .build();

        user.setTeacherProfile(teacher);

        User saved = userRepository.save(user);
        log.info("Teacher created: id={}, teacherId={}, email={}", saved.getId(), teacher.getTeacherId(), saved.getEmail());

        eventPublisher.publish(UserCreatedEvent.builder()
                .userId(saved.getId())
                .email(saved.getEmail())
                .fullName(saved.getFullName())
                .profileType(saved.getProfileType())
                .occurredOn(Instant.now())
                .build());

        return userProfileMapper.toResponse(saved);
    }

    private void validateEmailNotExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException(ErrorCode.IDENTITY_002, "Email déjà utilisé");
        }
    }

    private void validateUsernameNotExists(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new ConflictException(ErrorCode.IDENTITY_003, "Nom d'utilisateur déjà utilisé");
        }
    }

    private String generateTemporaryPassword() {
        StringBuilder sb = new StringBuilder(TEMP_PASSWORD_LENGTH);
        for (int i = 0; i < TEMP_PASSWORD_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    private String generateTeacherId() {
        String year = String.valueOf(LocalDate.now().getYear());
        long count = teacherProfileRepository.count() + 1;
        return String.format("TCH-%s-%05d", year, count);
    }
}