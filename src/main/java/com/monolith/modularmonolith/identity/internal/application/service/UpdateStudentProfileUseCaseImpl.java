package com.monolith.modularmonolith.identity.internal.application.service;

import com.monolith.modularmonolith.identity.internal.application.port.inbound.UpdateStudentProfileUseCase;
import com.monolith.modularmonolith.identity.internal.domain.model.StudentProfile;
import com.monolith.modularmonolith.identity.internal.domain.model.User;
import com.monolith.modularmonolith.identity.internal.domain.repository.UserRepository;
import com.monolith.modularmonolith.identity.internal.dto.request.UpdateStudentProfileRequest;
import com.monolith.modularmonolith.identity.internal.dto.response.UserProfileResponse;
import com.monolith.modularmonolith.identity.internal.mapper.UserProfileMapper;
import com.monolith.modularmonolith.shared.exception.ErrorCode;
import com.monolith.modularmonolith.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class UpdateStudentProfileUseCaseImpl implements UpdateStudentProfileUseCase {

    private final UserRepository userRepository;
    private final UserProfileMapper userProfileMapper;

    @Override
    @Transactional
    public UserProfileResponse execute(String email, UpdateStudentProfileRequest request) {
        User user = userRepository.findByEmailWithProfiles(email)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));

        updateBaseInfo(user, request);
        updateStudentInfo(user, request);
        user.setUpdatedAt(LocalDateTime.now());

        return userProfileMapper.toUserProfileResponse(userRepository.save(user));
    }

    private void updateBaseInfo(User user, UpdateStudentProfileRequest r) {
        if (r.firstName() != null) user.setFirstName(r.firstName());
        if (r.lastName() != null) user.setLastName(r.lastName());
        if (r.phone() != null) user.setPhone(r.phone());
        if (r.gender() != null) user.setGender(r.gender());
        if (r.birthDate() != null) user.setBirthDate(r.birthDate());
        if (r.nationality() != null) user.setNationality(r.nationality());
        if (r.language() != null) user.setLanguage(r.language());
        if (r.timezone() != null) user.setTimezone(r.timezone());
    }

    private void updateStudentInfo(User user, UpdateStudentProfileRequest r) {
        StudentProfile profile = user.getStudentProfile();
        if (profile == null) {
            profile = new StudentProfile();
            profile.setUser(user);
            user.setStudentProfile(profile);
        }
        if (r.parentName() != null) profile.setParentName(r.parentName());
        if (r.parentEmail() != null) profile.setParentEmail(r.parentEmail());
        if (r.parentPhone() != null) profile.setParentPhone(r.parentPhone());
        if (r.emergencyContact() != null) profile.setEmergencyContact(r.emergencyContact());
        if (r.emergencyContactPhone() != null) profile.setEmergencyContactPhone(r.emergencyContactPhone());
        if (r.address() != null) profile.setAddress(r.address());
        if (r.city() != null) profile.setCity(r.city());
        if (r.postalCode() != null) profile.setPostalCode(r.postalCode());
        if (r.country() != null) profile.setCountry(r.country());
        if (r.bloodGroup() != null) profile.setBloodGroup(r.bloodGroup());
        if (r.allergies() != null) profile.setAllergies(r.allergies());
        if (r.medicalNotes() != null) profile.setMedicalNotes(r.medicalNotes());
        if (r.extracurricularActivities() != null)
            profile.setExtracurricularActivities(new HashSet<>(r.extracurricularActivities()));
    }
}