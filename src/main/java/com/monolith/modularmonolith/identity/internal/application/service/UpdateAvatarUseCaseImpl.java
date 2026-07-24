package com.monolith.modularmonolith.identity.internal.application.service;

import com.monolith.modularmonolith.identity.internal.application.port.inbound.UpdateAvatarUseCase;
import com.monolith.modularmonolith.identity.internal.application.port.outbound.FileStorage;
import com.monolith.modularmonolith.identity.internal.domain.model.User;
import com.monolith.modularmonolith.identity.internal.domain.repository.UserRepository;
import com.monolith.modularmonolith.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

        // 1. Supprime l'ancien fichier sur disque
        String oldFilename = user.getAvatarFilename();
        if (oldFilename != null && !oldFilename.isBlank()) {
            fileStorage.delete(oldFilename);
        }

        // 2. Stocke le nouveau (retourne juste "uuid_nom.png")
        String newFilename = fileStorage.store(file, "avatars");

        // 3. 🔴 CRITIQUE : persiste le filename dans l'entité
        user.setAvatarFilename(newFilename);
        userRepository.save(user);
    }
}