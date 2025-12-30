package com.ksana.dto.request;

import jakarta.validation.constraints.NotBlank;

public class UpdateUserRequest {

    @NotBlank
    private String name;

    public String getName() {
        return name;
    }
}
