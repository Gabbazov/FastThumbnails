package ru.rgbb.fastthumbnails;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageProcessor {

    private final ImageReader imageReader;
    private final ImageResizer imageResizer;
    private final ImageWriter imageWriter;

    public void process() {

        imageReader.getFilesFromCurrentDirectory().stream()
                .map(imageResizer::resize)
                .forEach(imageWriter::writeToCurrentDireectory);
        ;
    }
}
