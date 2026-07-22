package com.monolith.modularmonolith.users.internal.mapper;

import com.monolith.modularmonolith.users.internal.dto.response.MeResponse;
import com.monolith.modularmonolith.users.internal.dto.response.MeResponse.StudentInfo;
import com.monolith.modularmonolith.users.internal.dto.response.MeResponse.TeacherInfo;
import com.monolith.modularmonolith.users.internal.dto.response.MeResponse.AdminInfo;
import com.monolith.modularmonolith.users.internal.dto.response.UserProfileSummaryResponse;
import com.monolith.modularmonolith.users.internal.model.AdminProfile;
import com.monolith.modularmonolith.users.internal.model.SchoolUser;
import com.monolith.modularmonolith.users.internal.model.StudentProfile;
import com.monolith.modularmonolith.users.internal.model.TeacherProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper MapStruct pour la conversion entre entités et DTOs.
 * L'implémentation est générée automatiquement à la compilation.
 */
@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    UserProfileMapper INSTANCE = Mappers.getMapper(UserProfileMapper.class);

    // ========== MeResponse (profil complet) ==========

    @Mapping(target = "displayName", expression = "java(buildDisplayName(user))")
    @Mapping(target = "student", source = "studentProfile", qualifiedByName = "mapStudentInfo")
    @Mapping(target = "teacher", source = "teacherProfile", qualifiedByName = "mapTeacherInfo")
    @Mapping(target = "admin", source = "adminProfile", qualifiedByName = "mapAdminInfo")
    @Mapping(target = "parent", ignore = true)
    MeResponse toMeResponse(SchoolUser user);

    // ========== UserProfileSummaryResponse (liste paginée) ==========

    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "profileType", source = "profileType")
    @Mapping(target = "gradeLevel", expression = "java(extractGradeLevel(user))")
    @Mapping(target = "department", expression = "java(extractDepartment(user))")
    @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
    UserProfileSummaryResponse toUserProfileSummaryResponse(SchoolUser user);

    // ========== Méthodes @Named pour les nested records ==========

    @Named("mapStudentInfo")
    default StudentInfo mapStudentInfo(StudentProfile profile) {
        if (profile == null) return null;
        return new StudentInfo(
                profile.getStudentId(),
                profile.getRegistrationNumber(),
                profile.getGradeLevel(),
                profile.getClassName(),
                profile.getSection(),
                profile.getAcademicYear(),
                profile.getUser() != null ? profile.getUser().getBirthDate() : null,
                profile.getPlaceOfBirth(),
                profile.getParentName(),
                profile.getParentPhone(),
                profile.getParentEmail(),
                profile.getEmergencyContact(),
                profile.getEmergencyContactPhone(),
                profile.getAddress(),
                profile.getCity(),
                profile.getPostalCode(),
                profile.getCountry(),
                profile.getEnrollmentDate(),
                profile.getGraduationDate(),
                profile.isScholarship(),
                profile.getScholarshipType(),
                profile.getGpa() != null ? profile.getGpa() : 0.0,
                profile.getAttendanceRate() != null ? profile.getAttendanceRate() : 0,
                profile.getBloodGroup(),
                profile.getAllergies(),
                profile.getMedicalNotes(),
                profile.getExtracurricularActivities()
        );
    }

    @Named("mapTeacherInfo")
    default TeacherInfo mapTeacherInfo(TeacherProfile profile) {
        if (profile == null) return null;
        return new TeacherInfo(
                profile.getTeacherId(),
                profile.getEmployeeId(),
                profile.getDepartment(),
                profile.getSpecialization(),
                profile.getSubjects(),
                profile.getClassesAssigned(),
                profile.getHireDate(),
                profile.getContractEndDate(),
                profile.getContractType(),
                profile.getQualification(),
                profile.getCertifications(),
                profile.getPhone(),
                profile.getOfficeLocation(),
                profile.getOfficeHours(),
                profile.getBio(),
                profile.getResearchInterests(),
                profile.getYearsOfExperience() != null ? profile.getYearsOfExperience() : 0,
                profile.isTenured(),
                profile.isDepartmentHead(),
                0.0,
                null,
                null
        );
    }

    @Named("mapAdminInfo")
    default AdminInfo mapAdminInfo(AdminProfile profile) {
        if (profile == null) return null;
        return new AdminInfo(
                profile.getAdminId(),
                profile.getDepartment(),
                profile.getJobTitle(),
                profile.getHireDate(),
                profile.getAccessLevel(),
                profile.getManagedModules(),
                profile.isCanManageUsers(),
                profile.isCanManageFinances(),
                profile.isCanManageAcademics(),
                profile.getOfficePhone(),
                profile.getOfficeLocation()
        );
    }

    // ========== Méthodes utilitaires pour le summary ==========

    default String extractGradeLevel(SchoolUser user) {
        if (user.getStudentProfile() != null) {
            return user.getStudentProfile().getGradeLevel();
        }
        return null;
    }

    default String extractDepartment(SchoolUser user) {
        if (user.getTeacherProfile() != null) {
            return user.getTeacherProfile().getDepartment();
        }
        if (user.getAdminProfile() != null) {
            return user.getAdminProfile().getDepartment();
        }
        return null;
    }

    // ========== Méthodes utilitaires partagées ==========

    default String buildDisplayName(SchoolUser user) {
        if (user.getFirstName() != null && user.getLastName() != null) {
            return user.getFirstName() + " " + user.getLastName();
        }
        return user.getUsername();
    }

    default Set<String> mapRoles(Set<String> roles) {
        if (roles == null) return java.util.Collections.emptySet();
        return roles.stream()
                .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role)
                .collect(Collectors.toSet());
    }
}