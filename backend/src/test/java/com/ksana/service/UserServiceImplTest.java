package com.ksana.service;

import com.ksana.dto.request.CreateUserRequest;
import com.ksana.dto.request.UpdateUserRequest;
import com.ksana.entity.User;
import com.ksana.exception.ResourceNotFoundException;
import com.ksana.repository.UserRepository;
import com.ksana.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class UserServiceImplTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserServiceImpl service;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        service = new UserServiceImpl(userRepository, passwordEncoder);
    }

    @Test
    void create_shouldCreateUser() {
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("srikanth");
        req.setName("Srikanth");
        req.setEmail("sri@gmail.com");
        req.setPassword("pass123");
        req.setRole("sales");

        when(userRepository.existsByEmail("sri@gmail.com")).thenReturn(false);
        when(userRepository.existsByUsername("srikanth")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("ENC");

        User saved = new User(1L, "Srikanth", "sri@gmail.com", "srikanth", "ENC", "SALES");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        var resp = service.create(req);

        assertEquals(1L, resp.getId());
        assertEquals("SALES", resp.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void create_shouldFail_whenEmailExists() {
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("u1");
        req.setName("n1");
        req.setEmail("e1@gmail.com");
        req.setPassword("pass123");
        req.setRole("USER");

        when(userRepository.existsByEmail("e1@gmail.com")).thenReturn(true);

        //assertThrows(T.class, () -> service.create(req));
    }

    @Test
    void getById_shouldReturn() {
        User user = new User(1L, "Admin", "admin@gmail.com", "admin", "pwd", "ADMIN");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        var res = service.getById(1L);
        assertEquals("Admin", res.getName());
    }

    @Test
    void getById_shouldThrow_whenMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getById(99L));
    }

    @Test
    void update_shouldUpdateFields() {
        User user = new User(1L, "Old", "old@gmail.com", "olduser", "pwd", "ADMIN");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("new@gmail.com")).thenReturn(false);
        when(userRepository.existsByUsername("newuser")).thenReturn(false);

        UpdateUserRequest req = new UpdateUserRequest();
        req.setName("New");
        req.setEmail("new@gmail.com");
        req.setUsername("newuser");
        req.setRole("ADMIN");

        var res = service.update(1L, req);

        assertEquals("New", res.getName());
        assertEquals("ADMIN", res.getRole());
    }

    @Test
    void delete_shouldDelete() {
        User user = new User(1L, "A", "a@gmail.com", "a", "pwd", "USER");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        service.delete(1L);

        verify(userRepository).delete(user);
    }
}
