package com.mouwengineering.careersapi.dto.portal;

import java.math.BigDecimal;

public record HoursSummaryResponse(
        BigDecimal totalHours,
        BigDecimal pendingHours,
        BigDecimal approvedHours,
        BigDecimal rejectedHours,
        int entryCount
) {
}
