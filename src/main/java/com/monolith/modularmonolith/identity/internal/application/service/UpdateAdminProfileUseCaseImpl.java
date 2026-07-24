package com.monolith.modularmonolith.identity.internal.application.service;

import com.monolith.modularmonolith.identity.internal.application.port.inbound.UpdateAdminProfileUseCase;
import com.monolith.modularmonolith.identity.internal.domain.model.AdminProfile;
import com.monolith.modularmonolith.identity.internal.domain.model.User;
import com.monolith.modularmonolith.identity.internal.domain.repository.UserRepository;
import com.monolith.modularmonolith.identity.internal.dto.request.UpdateAdminProfileRequest;
import com.monolith.modularmonolith.identity.internal.dto.response.UserProfileResponse;
import com.monolith.modularmonolith.identity.internal.mapper.UserProfileMapper;
import com.monolith.modularmonolith.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateAdminProfileUseCaseImpl implements UpdateAdminProfileUseCase {

    private final UserRepository userRepository;
    private final UserProfileMapper mapper;

    @Override
    public UserProfileResponse execute(String email, UpdateAdminProfileRequest request) {
        User user = userRepository.findByEmailWithProfiles(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", email));

        AdminProfile profile = user.getAdminProfile();
        if (profile == null) {
            profile = AdminProfile.builder()
                    .user(user)
                    .build();
            user.setAdminProfile(profile);
        }

        // Mise à jour des champs admin
        if (request.department() != null) profile.setDepartment(request.department());
        if (request.accessLevel() != null) profile.setAccessLevel(request.accessLevel());
        if (request.permissions() != null) profile.setPermissions(request.permissions());  // ← String direct
        if (request.hireDate() != null) profile.setHireDate(request.hireDate());            // ← LocalDate direct
        if (request.jobTitle() != null) profile.setJobTitle(request.jobTitle());
        if (request.officeLocation() != null) profile.setOfficeLocation(request.officeLocation());

        // Mise à jour des champs utilisateur de base
        if (request.firstName() != null) user.setFirstName(request.firstName());
        if (request.lastName() != null) user.setLastName(request.lastName());
        if (request.phoneNumber() != null) user.setPhoneNumber(request.phoneNumber());

        User saved = userRepository.save(user);

        return mapper.toResponse(saved);  // ← CORRIGÉ : toResponse()
    }
}