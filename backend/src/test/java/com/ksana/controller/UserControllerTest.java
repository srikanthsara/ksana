package com.ksana.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksana.dto.request.CreateUserRequest;
import com.ksana.dto.response.UserResponse;
import com.ksana.service.UserService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
//import  org.springframework.security.test.context.support.WithMockUser;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;




@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
@Disabled
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private UserService userService;

    @Test
    //@WithMockUser(roles = "ADMIN")
    void create_shouldReturn201() throws Exception {
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("u1");
        req.setName("N1");
        req.setEmail("e1@gmail.com");
        req.setPassword("pass123");
        req.setRole("USER");

        UserResponse resp = new UserResponse(1L, "u1", "N1", "e1@gmail.com", "USER", LocalDateTime.now());
        when(userService.create(any())).thenReturn(resp);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    //@WithMockUser(roles = "USER")
    void create_shouldReturn403_forNonAdmin() throws Exception {
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("u1");
        req.setName("N1");
        req.setEmail("e1@gmail.com");
        req.setPassword("pass123");
        req.setRole("USER");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }
}
