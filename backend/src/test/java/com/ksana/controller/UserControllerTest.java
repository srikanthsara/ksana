package com.ksana.controller;

import com.ksana.dto.request.CreateUserRequest;
import com.ksana.dto.response.UserResponse;
import com.ksana.service.UserService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Disabled
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    CreateUserRequest request;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser_shouldReturn201() throws Exception {
       // CreateUserRequest request = new CreateUserRequest();
        request.setName("John");
        request.setEmail("john@test.com");
        request.setPassword("secret");

        UserResponse response =
                new UserResponse(1L, "John", "john@test.com","sales");

        when(userService.create(request)).thenReturn(response);

        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("john@test.com"));
    }
}
