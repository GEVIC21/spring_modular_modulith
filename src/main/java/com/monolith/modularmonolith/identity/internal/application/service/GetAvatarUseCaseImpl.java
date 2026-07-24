package com.monolith.modularmonolith.identity.internal.application.service;

import com.monolith.modularmonolith.identity.internal.application.port.inbound.GetAvatarUseCase;
import com.monolith.modularmonolith.identity.internal.domain.model.User;
import com.monolith.modularmonolith.identity.internal.domain.repository.UserRepository;
import com.monolith.modularmonolith.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAvatarUseCaseImpl implements GetAvatarUseCase {

    private final UserRepository userRepository;

    @Override
    public String getFilename(String email) {
        return userRepository.findByEmail(email)
                .map(User::getAvatarFilename)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", email));
    }

    @Override
    public void delete(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", email));
        user.setAvatarFilename(null);
        userRepository.save(user);
    }
}