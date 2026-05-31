package com.pviengineers.careersapi.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Username is required")
        @Size(max = 150, message = "Username is too long")
        String username,
        @NotBlank(message = "Password is required")
        @Size(max = 256, message = "Password is too long")
        String password
) {
}
