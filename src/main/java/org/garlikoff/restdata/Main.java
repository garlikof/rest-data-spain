package org.garlikoff.restdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Rest Data Spain Spring Boot application.
 */
@SpringBootApplication
public class Main {

    /**
     * Bootstraps the Spring Boot application.
     *
     * @param args the application arguments passed from the command line
     */
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
