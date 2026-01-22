package com.ksana.repository;

import com.ksana.entity.User;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void existsByEmail_shouldReturnTrue() {
        User user = new User(
                null,
                "John",
                "john@test.com",
                "secret",
                "sales"
        );

        userRepository.save(user);

        boolean exists =
                userRepository.existsByEmail("john@test.com");

        assertTrue(exists);
    }
}
