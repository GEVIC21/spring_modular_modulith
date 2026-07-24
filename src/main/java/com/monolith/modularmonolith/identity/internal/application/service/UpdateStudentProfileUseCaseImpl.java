package com.monolith.modularmonolith.identity.internal.application.service;

import com.monolith.modularmonolith.identity.internal.application.port.inbound.UpdateStudentProfileUseCase;
import com.monolith.modularmonolith.identity.internal.domain.model.StudentProfile;
import com.monolith.modularmonolith.identity.internal.domain.model.User;
import com.monolith.modularmonolith.identity.internal.domain.repository.StudentProfileRepository;
import com.monolith.modularmonolith.identity.internal.domain.repository.UserRepository;
import com.monolith.modularmonolith.identity.internal.dto.request.UpdateStudentProfileRequest;
import com.monolith.modularmonolith.identity.internal.dto.response.UserProfileResponse;
import com.monolith.modularmonolith.identity.internal.mapper.UserProfileMapper;
import com.monolith.modularmonolith.shared.exception.ErrorCode;
import com.monolith.modularmonolith.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateStudentProfileUseCaseImpl implements UpdateStudentProfileUseCase {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final UserProfileMapper userProfileMapper;

    @Override
    @Transactional
    public UserProfileResponse execute(String email, UpdateStudentProfileRequest request) {
        User user = userRepository.findByEmailWithProfiles(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.IDENTITY_001, "Utilisateur introuvable avec l'email : " + email));

        updateBaseInfo(user, request);
        updateStudentInfo(user, request);

        User saved = userRepository.save(user);
        log.info("Student profile updated for {}", email);
        return userProfileMapper.toUserProfileResponse(saved);
    }

    private void updateBaseInfo(User user, UpdateStudentProfileRequest r) {
        if (r.firstName() != null) user.setFirstName(r.firstName());
        if (r.lastName() != null) user.setLastName(r.lastName());
        if (r.phoneNumber() != null) user.setPhoneNumber(r.phoneNumber());
        if (r.gender() != null) user.setGender(r.gender());
        if (r.dateOfBirth() != null) user.setDateOfBirth(r.dateOfBirth());
        if (r.nationality() != null) user.setNationality(r.nationality());
        if (r.language() != null) user.setLanguage(r.language());
    }

    private void updateStudentInfo(User user, UpdateStudentProfileRequest r) {
        StudentProfile profile = user.getStudentProfile();

        if (profile == null) {
            profile = StudentProfile.builder()
                    .user(user)
                    .build();
            user.setStudentProfile(profile);
        }

        if (r.gradeLevel() != null) profile.setGradeLevel(r.gradeLevel());
        if (r.className() != null) profile.setClassName(r.className());
        if (r.enrollmentDate() != null) profile.setEnrollmentDate(r.enrollmentDate());
        if (r.parentName() != null) profile.setParentName(r.parentName());
        if (r.parentEmail() != null) profile.setParentEmail(r.parentEmail());
        if (r.parentPhone() != null) profile.setParentPhone(r.parentPhone());
        if (r.emergencyContact() != null) profile.setEmergencyContact(r.emergencyContact());
        if (r.emergencyPhone() != null) profile.setEmergencyPhone(r.emergencyPhone());
        if (r.address() != null) profile.setAddress(r.address());
        if (r.city() != null) profile.setCity(r.city());
        if (r.postalCode() != null) profile.setPostalCode(r.postalCode());
        if (r.country() != null) profile.setCountry(r.country());
        if (r.bloodGroup() != null) profile.setBloodGroup(r.bloodGroup());
        if (r.allergies() != null) profile.setAllergies(r.allergies());
        if (r.medicalNotes() != null) profile.setMedicalNotes(r.medicalNotes());
        if (r.extracurricularActivities() != null) {
            profile.setExtracurricularActivities(new HashSet<>(r.extracurricularActivities()));
        }
    }
}