package com.contatodo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Contatodo application.
 */
@SpringBootApplication
public class ContatodoApplication {

    /**
     * Starts the Spring Boot application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(ContatodoApplication.class, args);
    }
}
