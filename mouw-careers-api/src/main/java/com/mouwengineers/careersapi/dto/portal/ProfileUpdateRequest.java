package com.mouwengineering.careersapi.dto.portal;

import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
        @Size(max = 40) String phone
) {
}
