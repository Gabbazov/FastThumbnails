package ru.rgbb.fastthumbnails;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Log4j2
@RequiredArgsConstructor
public class ImageReader {

    private final Predicate<File> supportedExtensionPredicate;

    @Value("${thumbnail.name:thumbnail}")
    private String thumbnailName;

    @Nonnull
    public Collection<Path> getFilesFromCurrentDirectory() {

        var currentDir = new File(StringUtils.EMPTY);
        log.info("Current dir: {}", currentDir.getAbsolutePath());

        try (Stream<Path> stream = Files.list(Paths.get(currentDir.getAbsolutePath()))) {
            var filesInFolder = stream
                    .filter(path -> !Files.isDirectory(path) &&
                            !StringUtils.containsIgnoreCase(path.getFileName().toString(), thumbnailName) &&
                            supportedExtensionPredicate.test(path.toFile()))
                    .collect(Collectors.toSet());
            log.info("Got files to resize: {}", filesInFolder);
            return filesInFolder;
        } catch (IOException e) {
            log.error("Exception: ", e);
            return new HashSet<>();
        }
    }
}
