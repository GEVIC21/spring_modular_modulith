package com.monolith.modularmonolith.identity.internal.application.service;

import com.monolith.modularmonolith.identity.internal.application.port.inbound.UpdateAvatarUseCase;
import com.monolith.modularmonolith.identity.internal.application.port.outbound.FileStorage;
import com.monolith.modularmonolith.identity.internal.domain.model.User;
import com.monolith.modularmonolith.identity.internal.domain.repository.UserRepository;
import com.monolith.modularmonolith.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UpdateAvatarUseCaseImpl implements UpdateAvatarUseCase {

    private final UserRepository userRepository;
    private final FileStorage fileStorage;

    @Override
    public void execute(String email, MultipartFile file) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", email));

        // Supprime l'ancien avatar si présent
        if (user.getAvatarFilename() != null) {
            fileStorage.delete(user.getAvatarFilename());
        }

        String filename = fileStorage.store(file, "avatars");
        user.setAvatarFilename(filename);
        userRepository.save(user);

        log.info("Avatar updated for {}", email);
    }
}