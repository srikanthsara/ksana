package com.ksana.config;

import com.ksana.entity.User;
import com.ksana.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initUsers(UserRepository userRepository,
                                       PasswordEncoder passwordEncoder) {

        return args -> {

            if (userRepository.existsByUsername("admin")) {
                return;
            }

            User user = new User();
            user.setUsername("admin");
            user.setName("Administrator");
            user.setEmail("admin@ksana.com");
            user.setPassword(passwordEncoder.encode("admin123"));
            user.setRole("ADMIN");
            user.setCreatedAt(LocalDateTime.now());

            userRepository.save(user);
        };
    }
}
