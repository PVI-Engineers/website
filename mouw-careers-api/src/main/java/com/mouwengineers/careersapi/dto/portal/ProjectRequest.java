package com.mouwengineering.careersapi.dto.portal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectRequest(
        @NotBlank @Size(max = 160) String name,
        @NotBlank @Size(max = 40) String code,
        @Size(max = 500) String description,
        Boolean active
) {
}
