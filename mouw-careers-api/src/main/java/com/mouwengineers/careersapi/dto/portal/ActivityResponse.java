package com.mouwengineering.careersapi.dto.portal;

import java.time.LocalDateTime;

public record ActivityResponse(
        Long id,
        String message,
        LocalDateTime occurredAt
) {
}
