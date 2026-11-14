package com.mouwengineering.careersapi.dto.portal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TimesheetResponse(
        Long id,
        Long employeeId,
        String employeeCode,
        String employeeName,
        Long projectId,
        String projectName,
        String projectCode,
        LocalDate workDate,
        Integer startMinute,
        Integer endMinute,
        BigDecimal hours,
        String workDescription,
        String status,
        LocalDateTime submittedAt,
        LocalDateTime reviewedAt,
        String reviewNote
) {
}
