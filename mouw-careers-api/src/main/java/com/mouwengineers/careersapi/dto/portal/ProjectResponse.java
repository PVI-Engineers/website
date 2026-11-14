package com.mouwengineering.careersapi.dto.portal;

public record ProjectResponse(
        Long id,
        String name,
        String code,
        String description,
        boolean active
) {
}
