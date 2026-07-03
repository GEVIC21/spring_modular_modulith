package com.monolith.modularmonolith.users.internal.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
public class AvatarGeneratorService {

    private static final List<String> PALETTE = List.of(
            "#1abc9c", "#2ecc71", "#3498db", "#9b59b6", "#34495e",
            "#16a085", "#27ae60", "#2980b9", "#8e44ad", "#2c3e50",
            "#f1c40f", "#e67e22", "#e74c3c", "#7f8c8d", "#95a5a6"
    );

    public String generateAvatarDataUri(String username) {
        String initials = extractInitials(username);
        String color = selectColor(username);
        String svg = buildSvg(initials, color);
        String base64 = Base64.getEncoder().encodeToString(svg.getBytes(StandardCharsets.UTF_8));
        return "data:image/svg+xml;base64," + base64;
    }

    public byte[] generateAvatarSvgBytes(String username) {
        String svg = buildSvg(extractInitials(username), selectColor(username));
        return svg.getBytes(StandardCharsets.UTF_8);
    }

    private String buildSvg(String initials, String color) {
        return String.format(
                "<svg xmlns='http://www.w3.org/2000/svg' width='200' height='200' viewBox='0 0 200 200'>" +
                        "<circle cx='100' cy='100' r='100' fill='%s'/>" +
                        "<text x='100' y='125' font-family='Arial,sans-serif' font-size='90' " +
                        "font-weight='bold' text-anchor='middle' fill='#ffffff'>%s</text>" +
                        "</svg>", color, initials);
    }

    private String extractInitials(String username) {
        if (username == null || username.isBlank()) return "?";
        String clean = username.trim();
        String[] parts = clean.split("\\s+");
        if (parts.length >= 2) {
            return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
        }
        return clean.substring(0, Math.min(2, clean.length())).toUpperCase();
    }

    private String selectColor(String username) {
        int index = Math.abs(username.hashCode()) % PALETTE.size();
        return PALETTE.get(index);
    }
}