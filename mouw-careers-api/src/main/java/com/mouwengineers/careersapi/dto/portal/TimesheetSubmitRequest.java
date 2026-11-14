package com.mouwengineering.careersapi.dto.portal;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record TimesheetSubmitRequest(
        @NotNull Long projectId,
        @NotNull LocalDate workDate,
        @NotNull @Min(0) @Max(1410) Integer startMinute,
        @NotNull @Min(30) @Max(1440) Integer endMinute,
        @NotBlank @Size(min = 10, max = 2000) String workDescription
) {
}
