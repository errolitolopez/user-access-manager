package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * This is the main class that starts our Spring Boot application.
 *
 * @SpringBootApplication is a powerful annotation that handles the core setup for us,
 * so we don't have to manually configure everything.
 *
 * @EnableSpringDataWebSupport is the specific fix for the warning you saw. It tells Spring to
 * automatically convert your "Page" objects into a stable "PagedModel" for JSON responses.
 * This guarantees that your API's pagination format won't change, which is important for
 * any applications that rely on your data.
 */
@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@EnableScheduling
@EnableAsync
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
