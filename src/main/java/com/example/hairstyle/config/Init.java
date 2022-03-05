package com.example.hairstyle.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@Slf4j
public class Init {
    @Bean
    @Transactional
    CommandLineRunner commandLineRunner(
    ) {
        return args -> {
        };
    }
}
