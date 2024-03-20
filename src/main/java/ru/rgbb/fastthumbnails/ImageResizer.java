package ru.rgbb.fastthumbnails;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.exif.ExifIFD0Directory;
import jakarta.annotation.Nonnull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

@Component
@Log4j2
public class ImageResizer {

    @Value("${thumbnail.height:200}")
    private int thumbnailHeight;

    @Nonnull
    public BufferedImage resize(@Nonnull final Path path) {

        log.info("Resize file {}", path.getFileName());
        try {
            return getResizedImage(path.toFile());
        } catch (IOException e) {
            log.error("Exception: ", e);
            throw new IllegalArgumentException("Exception while resize image " + path.getFileName() + ": " + e.getMessage());
        }
    }

    @Nonnull
    private BufferedImage getResizedImage(@Nonnull final File file) throws IOException {

        int rotationIndex = determineRotationFromExif(file);
        try (var is = new FileInputStream(file)) {
            var resizedImage = getResizedImage(is);
            switch (rotationIndex) {
                case 8 -> {
                    return getRotatedImage(resizedImage, 270d);
                }
                case 3 -> {
                    return getRotatedImage(resizedImage, 180d);
                }
                case 6 -> {
                    return getRotatedImage(resizedImage, 90d);
                }
                default -> {
                    return resizedImage;
                }
            }
        }
    }

    /**
     * Resize image keeping proportions.
     *
     * @throws IOException
     */
    @Nonnull
    private BufferedImage getResizedImage(final InputStream is) throws IOException {

        final var originalImage = ImageIO.read(is);

        double origWidth = originalImage.getWidth();
        double origHeight = originalImage.getHeight();
        double ratio = Math.max(origWidth, origHeight) / Math.min(origWidth, origHeight);

        int newHeight = thumbnailHeight;
        int newWidth = (int) Math.round(thumbnailHeight * ratio);

        final var resultingImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

        final var outputImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);

        return outputImage;
    }

    /**
     * Rotate given image to given angle
     */
    @Nonnull
    private static BufferedImage getRotatedImage(@Nonnull final BufferedImage outputImage,
                                                 final double angle) {

        double radian = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(radian));
        double cos = Math.abs(Math.cos(radian));
        int origWidth = outputImage.getWidth();
        int origHeight = outputImage.getHeight();
        int nWidth = (int) Math.floor(origWidth * cos + origHeight * sin);
        int nHeight = (int) Math.floor(origHeight * cos + origWidth * sin);

        var rotated = new BufferedImage(nWidth, nHeight, BufferedImage.TYPE_INT_RGB);
        var graphic = rotated.createGraphics();
        graphic.translate((nWidth - origWidth) / 2, (nHeight - origHeight) / 2);
        graphic.rotate(radian, (double) origWidth / 2, (double) origHeight / 2);
        graphic.drawImage(outputImage, null, 0, 0);
        graphic.dispose();

        return rotated;
    }

    /**
     * Try determine rotation from EXIF. 0 in case of errors.
     */
    private int determineRotationFromExif(@Nonnull final File file) {

        try (var is = new FileInputStream(file)) {
            var orientationExifDir = ImageMetadataReader.readMetadata(is)
                    .getFirstDirectoryOfType(ExifIFD0Directory.class);
            return orientationExifDir.getTags().stream()
                    .filter(t -> StringUtils.equalsIgnoreCase(t.getTagName(), "Orientation"))
                    .findFirst()
                    .map(t -> orientationExifDir.getInteger(t.getTagType()))
                    .orElse(0);
        } catch (Exception e) {
            return 0;
        }
    }

}
