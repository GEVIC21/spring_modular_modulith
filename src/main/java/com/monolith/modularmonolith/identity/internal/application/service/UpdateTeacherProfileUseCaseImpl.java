package com.monolith.modularmonolith.identity.internal.application.service;

import com.monolith.modularmonolith.identity.internal.application.port.inbound.UpdateTeacherProfileUseCase;
import com.monolith.modularmonolith.identity.internal.domain.model.TeacherProfile;
import com.monolith.modularmonolith.identity.internal.domain.model.User;
import com.monolith.modularmonolith.identity.internal.domain.repository.TeacherProfileRepository;
import com.monolith.modularmonolith.identity.internal.domain.repository.UserRepository;
import com.monolith.modularmonolith.identity.internal.dto.request.UpdateTeacherProfileRequest;
import com.monolith.modularmonolith.identity.internal.dto.response.UserProfileResponse;
import com.monolith.modularmonolith.identity.internal.mapper.UserProfileMapper;
import com.monolith.modularmonolith.shared.exception.ErrorCode;
import com.monolith.modularmonolith.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UpdateTeacherProfileUseCaseImpl implements UpdateTeacherProfileUseCase {

    private final UserRepository userRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final UserProfileMapper mapper;

    @Override
    public UserProfileResponse execute(String email, UpdateTeacherProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", email));

        TeacherProfile profile = user.getTeacherProfile();
        if (profile == null) {
            throw new ResourceNotFoundException("Profil enseignant", "email=" + email);
        }

        if (request.firstName() != null) user.setFirstName(request.firstName());
        if (request.lastName() != null) user.setLastName(request.lastName());
        if (request.phoneNumber() != null) user.setPhoneNumber(request.phoneNumber());

        if (request.department() != null) profile.setDepartment(request.department());
        if (request.subjects() != null) profile.setSubjects(request.subjects());
        if (request.classesAssigned() != null) profile.setClassesAssigned(request.classesAssigned());
        if (request.qualification() != null) profile.setQualification(request.qualification());
        if (request.hireDate() != null) profile.setHireDate(request.hireDate());
        if (request.specialization() != null) profile.setSpecialization(request.specialization());
        if (request.officeLocation() != null) profile.setOfficeLocation(request.officeLocation());
        if (request.officeHours() != null) profile.setOfficeHours(request.officeHours());

        userRepository.save(user);
        teacherProfileRepository.save(profile);

        log.info("Profil enseignant mis à jour: {}", email);
        return mapper.toResponse(user);
    }
}