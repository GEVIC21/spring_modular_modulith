package com.monolith.modularmonolith.identity.internal.application.service;

import com.monolith.modularmonolith.identity.internal.application.port.inbound.DeleteUserUseCase;
import com.monolith.modularmonolith.identity.internal.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteUserUseCaseImpl implements DeleteUserUseCase {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void execute(Long userId) {
        userRepository.deleteById(userId);
        log.warn("User permanently deleted: {}", userId);
    }
}