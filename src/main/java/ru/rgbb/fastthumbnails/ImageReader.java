package ru.rgbb.fastthumbnails;

import jakarta.annotation.Nonnull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Log4j2
public class ImageReader {


    @Value("${supported.extensions:png}")
    private String supportedExtensions;

    @Nonnull
    public Collection<File> getFilesFromCurrentDirectory() {

        var currentDir = new File("");
        log.info("Current dir: {}", currentDir.getAbsolutePath());

        var extensions = Arrays.asList(supportedExtensions.split(","));
        log.info("Supported extensions: {}", extensions);

        var newFile = new File("testfile.txt");
        try {
            if (newFile.exists()) {
                newFile.delete();
            }
            newFile.createNewFile();
        } catch (IOException e) {
            log.error("Exception: ", e);
        }

        try (Stream<Path> stream = Files.list(Paths.get(currentDir.getAbsolutePath()))) {
            var filesInFolder = stream
                    .filter(path -> !Files.isDirectory(path) &&
                            extensions.stream().anyMatch(path.toFile().getName().toLowerCase()::endsWith))
                    .map(Path::toFile)
                    .collect(Collectors.toSet());
            log.info("Got files: {}", filesInFolder);
            return filesInFolder;
        } catch (IOException e) {
            log.error("Exception: ", e);
            return new HashSet<>();
        }
    }
}
