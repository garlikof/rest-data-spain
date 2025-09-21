package org.garlikoff.restdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Точка входа в приложение Rest Data Spain на Spring Boot.
 */
@SpringBootApplication
public class Main {

    /**
     * Инициализирует приложение Spring Boot.
     *
     * @param args аргументы приложения, переданные из командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
