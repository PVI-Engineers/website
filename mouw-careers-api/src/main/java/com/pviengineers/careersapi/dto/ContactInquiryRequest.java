package com.pviengineers.careersapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContactInquiryRequest(
        @NotBlank(message = "Name is required.")
        @Size(max = 120, message = "Name must be at most 120 characters.")
        String name,

        @NotBlank(message = "Email is required.")
        @Email(message = "Email must be valid.")
        @Size(max = 150, message = "Email must be at most 150 characters.")
        String email,

        @NotBlank(message = "Phone is required.")
        @Size(max = 30, message = "Phone must be at most 30 characters.")
        String phone,

        @Size(max = 150, message = "Company must be at most 150 characters.")
        String company,

        @NotBlank(message = "Inquiry type is required.")
        @Size(max = 100, message = "Inquiry type must be at most 100 characters.")
        String inquiryType,

        @NotBlank(message = "Message is required.")
        @Size(max = 2000, message = "Message must be at most 2000 characters.")
        String message
) {
}
