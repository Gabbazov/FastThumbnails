package ru.rgbb.fastthumbnails;

import jakarta.annotation.Nonnull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Component
@Log4j2
public class ImageWriter {


    @Value("${thumbnail.name:thumbnail}")
    private String thumbnailName;

    void writeToCurrentDirectory(@Nonnull final String sourceFilename,
                                 @Nonnull final BufferedImage bufferedImage) {

        log.info("Write fo current directory resized image for file {}", sourceFilename);
        
        var outputfile = new File(
                String.format("%s_%s.%s",
                        FilenameUtils.removeExtension(sourceFilename),
                        thumbnailName,
                        FilenameUtils.getExtension(sourceFilename)));

        try {
            ImageIO.write(bufferedImage, FilenameUtils.getExtension(sourceFilename), outputfile);
        } catch (IOException e) {
            log.error("Exception: ", e);
            throw new IllegalArgumentException("Exception while write image " + outputfile.getName() + ": " + e.getMessage());
        }

    }
}
