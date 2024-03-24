package ru.rgbb.fastthumbnails;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.function.Predicate;

@Component
public class SupportedExtensionPredicate implements Predicate<File> {

    @Value("${supported.extensions:png}")
    private String supportedExtensions;

    @Override
    public boolean test(File file) {
        return Arrays.stream(supportedExtensions.split(","))
                .anyMatch(file.getName().toLowerCase()::endsWith);
    }
}
