package com.mouwengineering.careersapi.dto.portal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TimesheetReviewRequest(
        @NotBlank String decision,
        @Size(max = 500) String note
) {
}
