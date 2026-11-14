package com.mouwengineering.careersapi.dto.portal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TimesheetSubmitRequest(
        @NotNull Long projectId,
        @NotNull LocalDate workDate,
        @NotNull @DecimalMin("0.25") @DecimalMax("24.00") @Digits(integer = 2, fraction = 2) BigDecimal hours,
        @NotBlank @Size(min = 10, max = 2000) String workDescription
) {
}
