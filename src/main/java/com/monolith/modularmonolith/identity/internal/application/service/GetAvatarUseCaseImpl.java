package com.monolith.modularmonolith.identity.internal.application.service;

import com.monolith.modularmonolith.identity.internal.application.port.inbound.GetAvatarUseCase;
import com.monolith.modularmonolith.identity.internal.domain.model.User;
import com.monolith.modularmonolith.identity.internal.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAvatarUseCaseImpl implements GetAvatarUseCase {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public String getFilename(String email) {
        return userRepository.findByEmail(email)
                .map(User::getAvatarUrl)
                .orElse(null);
    }

    @Override
    @Transactional
    public void delete(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            user.setAvatarUrl(null);
            userRepository.save(user);
        }
    }
}