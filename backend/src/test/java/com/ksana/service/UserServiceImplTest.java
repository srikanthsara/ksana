package com.ksana.service;

import com.ksana.dto.request.CreateUserRequest;
import com.ksana.dto.response.UserResponse;
import com.ksana.exception.ResourceNotFoundException;
import com.ksana.model.entity.User;
import com.ksana.repository.UserRepository;
import com.ksana.service.impl.UserServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.when;


class UserServiceImplTest {

    private UserRepository userRepository;
    private UserServiceImpl userService;

    @BeforeEach
    void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void createUser_shouldCreateSuccessfully() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("John");
        request.setEmail("john@test.com");
        request.setPassword("secret");

        when(userRepository.existsByEmail("john@test.com"))
                .thenReturn(false);

        when(userRepository.save(Mockito.any(User.class)))
                .thenAnswer(invocation -> {
                    User u = invocation.getArgument(0);
                    return new User(1L, u.getName(), u.getEmail(), u.getPassword());
                });

        UserResponse response = userService.create(request);

        assertEquals("John", response.getName());
        assertEquals("john@test.com", response.getEmail());
    }

    @Test
    void getUser_shouldThrowException_whenNotFound() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getById(1L)
        );
    }
}
