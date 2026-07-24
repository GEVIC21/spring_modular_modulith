package com.monolith.modularmonolith.identity.internal.application.service;

import com.monolith.modularmonolith.identity.internal.application.port.inbound.GetUserStatisticsUseCase;
import com.monolith.modularmonolith.identity.internal.domain.model.ProfileType;
import com.monolith.modularmonolith.identity.internal.domain.repository.UserRepository;
import com.monolith.modularmonolith.identity.internal.dto.response.UserStatistics;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetUserStatisticsUseCaseImpl implements GetUserStatisticsUseCase {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserStatistics execute() {
        long total = userRepository.count();
        long students = userRepository.countByProfileType(ProfileType.STUDENT);
        long teachers = userRepository.countByProfileType(ProfileType.TEACHER);
        long admins = userRepository.countByProfileType(ProfileType.ADMIN);
        long superAdmins = userRepository.countByProfileType(ProfileType.SUPER_ADMIN);
        long parents = userRepository.countByProfileType(ProfileType.PARENT);
        long active = userRepository.countByActive(true);
        long inactive = userRepository.countByActive(false);

        return UserStatistics.builder()
                .totalUsers(total)
                .totalStudents(students)
                .totalTeachers(teachers)
                .totalAdmins(admins)
                .totalSuperAdmins(superAdmins)
                .totalParents(parents)
                .activeUsers(active)
                .inactiveUsers(inactive)
                .build();
    }
}