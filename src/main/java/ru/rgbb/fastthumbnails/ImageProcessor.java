package ru.rgbb.fastthumbnails;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class ImageProcessor {

    private final ImageReader imageReader;
    private final ImageResizer imageResizer;
    private final ImageWriter imageWriter;

    public void process() {

        log.info("Start processing");
        imageReader.getFilesFromCurrentDirectory().stream()
                .map(imageResizer::resize)
                .forEach(imageWriter::writeToCurrentDireectory);
        log.info("Finish processing");
    }
}
