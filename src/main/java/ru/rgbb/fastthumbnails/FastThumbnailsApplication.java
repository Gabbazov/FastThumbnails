package ru.rgbb.fastthumbnails;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class FastThumbnailsApplication {

    public static void main(String[] args) {
        final ApplicationContext ctx = SpringApplication.run(FastThumbnailsApplication.class, args);
        ((ImageProcessor) ctx.getBean("imageProcessor")).process();
    }

}
