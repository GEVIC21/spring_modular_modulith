package com.monolith.modularmonolith.users.internal.service;

import com.monolith.modularmonolith.users.internal.dto.ProfileResponse;
import com.monolith.modularmonolith.users.internal.dto.UpdateProfileRequest;
import com.monolith.modularmonolith.users.internal.dto.UploadAvatarResponse;
import com.monolith.modularmonolith.users.internal.model.Permission;
import com.monolith.modularmonolith.users.internal.model.Role;
import com.monolith.modularmonolith.users.internal.model.User;
import com.monolith.modularmonolith.users.internal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final AvatarGeneratorService avatarGeneratorService;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public ProfileResponse getProfileByEmail(String email) {
        User user = findUserOrThrow(email);
        return mapToProfileResponse(user);
    }

    @Transactional
    public ProfileResponse updateProfile(String currentEmail, UpdateProfileRequest request) {
        User user = findUserOrThrow(currentEmail);

        if (!user.getPublicUsername().equals(request.username()) && userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Ce nom d'utilisateur est déjà pris.");
        }

        if (!user.getEmail().equals(request.email()) && userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Cet e-mail est déjà utilisé.");
        }

        user.setUsername(request.username());
        user.setEmail(request.email());

        User updatedUser = userRepository.save(user);
        return mapToProfileResponse(updatedUser);
    }

    @Transactional
    public UploadAvatarResponse updateAvatar(String email, MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide.");
        }

        User user = findUserOrThrow(email);

        // Supprime l'ancien fichier s'il existe
        if (user.getAvatarUrl() != null) {
            fileStorageService.delete(user.getAvatarUrl());
        }

        String filename = fileStorageService.store(file, "user_" + user.getId());
        user.setAvatarUrl(filename);
        userRepository.save(user);

        return new UploadAvatarResponse("Photo de profil mise à jour.", filename);
    }

    @Transactional
    public void deleteAvatar(String email) {
        User user = findUserOrThrow(email);
        if (user.getAvatarUrl() != null) {
            fileStorageService.delete(user.getAvatarUrl());
            user.setAvatarUrl(null);
            userRepository.save(user);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Resource> loadAvatar(String email) {
        User user = findUserOrThrow(email);

        if (user.getAvatarUrl() != null) {
            Resource resource = fileStorageService.loadAsResource(user.getAvatarUrl());
            String contentType = fileStorageService.getContentType(user.getAvatarUrl());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } else {
            byte[] svgBytes = avatarGeneratorService.generateAvatarSvgBytes(user.getPublicUsername());
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf("image/svg+xml"))
                    .body(new ByteArrayResource(svgBytes));
        }
    }

    private User findUserOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email));
    }

    private ProfileResponse mapToProfileResponse(User user) {
        var roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        var permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());

        boolean hasCustom = user.getAvatarUrl() != null;
        String avatarUrl = hasCustom
                ? "/api/v1/users/me/avatar"  // Endpoint pour récupérer l'image binaire
                : avatarGeneratorService.generateAvatarDataUri(user.getPublicUsername());

        return new ProfileResponse(
                user.getId(),
                user.getPublicUsername(),
                user.getEmail(),
                roles,
                permissions,
                avatarUrl,
                hasCustom
        );
    }
}