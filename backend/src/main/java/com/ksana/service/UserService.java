package com.ksana.service;

import com.ksana.dto.request.CreateUserRequest;
import com.ksana.dto.request.UpdateUserRequest;
import com.ksana.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse create(CreateUserRequest request);

    UserResponse getById(Long id);

    List<UserResponse> getAll();

    UserResponse update(Long id, UpdateUserRequest request);

    void delete(Long id);
}
