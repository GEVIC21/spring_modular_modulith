package com.monolith.modularmonolith.users.internal.controller;

import com.monolith.modularmonolith.users.internal.service.AvatarService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * Contrôleur public pour servir les avatars sans authentification.
 * Utilisé pour l'affichage des avatars dans l'interface utilisateur.
 */
@RestController
@RequestMapping("/api/v1/public/avatars")
@RequiredArgsConstructor
public class PublicAvatarController {

    private final AvatarService avatarService;

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getAvatar(@PathVariable String filename) {
        Resource resource = avatarService.loadFileAsResource(filename);
        String contentType = avatarService.resolveContentType(filename);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .cacheControl(CacheControl.maxAge(24, TimeUnit.HOURS))
                .body(resource);
    }

    @GetMapping("/thumb/{filename}")
    public ResponseEntity<Resource> getThumbnail(@PathVariable String filename) {
        Resource resource = avatarService.loadFileAsResource("thumb_" + filename);
        String contentType = avatarService.resolveContentType(filename);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .cacheControl(CacheControl.maxAge(24, TimeUnit.HOURS))
                .body(resource);
    }
}