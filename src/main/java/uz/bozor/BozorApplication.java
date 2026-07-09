package uz.bozor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BozorApplication {
    public static void main(String[] args) {
        SpringApplication.run(BozorApplication.class, args);
    }
}
