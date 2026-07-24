package com.monolith.modularmonolith.identity.internal.adapter.web;

import com.monolith.modularmonolith.identity.internal.application.port.outbound.FileStorage;
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

@RestController
@RequestMapping("/api/v1/public/avatars")
@RequiredArgsConstructor
public class PublicAvatarController {

    private final FileStorage fileStorage;

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getAvatar(@PathVariable String filename) {
        Resource resource = fileStorage.load(filename);
        String contentType = fileStorage.resolveContentType(filename);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .cacheControl(CacheControl.maxAge(24, TimeUnit.HOURS))
                .body(resource);
    }

    @GetMapping("/thumb/{filename}")
    public ResponseEntity<Resource> getThumbnail(@PathVariable String filename) {
        Resource resource = fileStorage.load("thumb_" + filename);
        String contentType = fileStorage.resolveContentType(filename);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .cacheControl(CacheControl.maxAge(24, TimeUnit.HOURS))
                .body(resource);
    }
}