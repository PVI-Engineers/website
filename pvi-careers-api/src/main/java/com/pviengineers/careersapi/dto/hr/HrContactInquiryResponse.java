package com.pviengineers.careersapi.dto.hr;

import java.time.LocalDateTime;

public record HrContactInquiryResponse(
        Long id,
        String inquiryRef,
        String name,
        String email,
        String phone,
        String company,
        String inquiryType,
        String message,
        LocalDateTime createdAt
) {
}
