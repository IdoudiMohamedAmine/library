package org.example.librarybackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LibraryBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryBackendApplication.class, args);
    }

}
