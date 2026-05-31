package com.pviengineers.careersapi.dto.admin;

import java.time.LocalDateTime;
import java.util.Set;

public record InternalUserResponse(
        Long id,
        String username,
        String fullName,
        String email,
        Set<String> roles,
        LocalDateTime createdAt
) {
}
