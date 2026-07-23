package com.monolith.modularmonolith.identity.internal.application.service;

import com.monolith.modularmonolith.identity.internal.application.port.inbound.GetUserStatisticsUseCase;
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
        long students = userRepository.countByProfileType("STUDENT");
        long teachers = userRepository.countByProfileType("TEACHER");
        long admins = userRepository.countByProfileType("ADMIN");
        long active = userRepository.countByActive(true);
        long inactive = userRepository.countByActive(false);

        return new UserStatistics(total, students, teachers, admins, active, inactive);
    }
}