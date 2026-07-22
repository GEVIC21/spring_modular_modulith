package com.monolith.modularmonolith.users.internal.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
public class ImageProcessingService {

    /**
     * Redimensionne et optimise une image pour un avatar.
     */
    public byte[] processAvatar(InputStream inputStream, int maxWidth, int maxHeight) throws IOException {
        BufferedImage originalImage = ImageIO.read(inputStream);
        if (originalImage == null) {
            throw new IllegalArgumentException("Format d'image non supporté");
        }

        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // Calcul des nouvelles dimensions en conservant le ratio
        double widthRatio = (double) maxWidth / originalWidth;
        double heightRatio = (double) maxHeight / originalHeight;
        double ratio = Math.min(widthRatio, heightRatio);

        int newWidth = (int) (originalWidth * ratio);
        int newHeight = (int) (originalHeight * ratio);

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = resizedImage.createGraphics();

        // Configuration de la qualité
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        graphics.dispose();

        // Compression JPEG optimisée
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", outputStream);

        return outputStream.toByteArray();
    }

    /**
     * Crée un thumbnail carré centré.
     */
    public byte[] createThumbnail(InputStream inputStream, int size) throws IOException {
        BufferedImage originalImage = ImageIO.read(inputStream);
        if (originalImage == null) {
            throw new IllegalArgumentException("Format d'image non supporté");
        }

        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // Calcul pour un crop centré carré
        int cropSize = Math.min(originalWidth, originalHeight);
        int x = (originalWidth - cropSize) / 2;
        int y = (originalHeight - cropSize) / 2;

        BufferedImage croppedImage = originalImage.getSubimage(x, y, cropSize, cropSize);
        BufferedImage thumbnail = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = thumbnail.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.drawImage(croppedImage, 0, 0, size, size, null);
        graphics.dispose();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(thumbnail, "jpg", outputStream);

        return outputStream.toByteArray();
    }
}