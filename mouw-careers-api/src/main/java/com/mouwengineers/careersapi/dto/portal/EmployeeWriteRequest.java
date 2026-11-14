package com.mouwengineering.careersapi.dto.portal;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record EmployeeWriteRequest(
        @NotBlank @Size(max = 120) String fullName,
        @NotBlank @Size(max = 40) String employeeCode,
        @NotBlank @Email @Size(max = 160) String email,
        @NotBlank @Size(max = 80) String designation,
        @NotBlank @Size(max = 80) String department,
        @Size(max = 72) String password,
        @NotEmpty List<Long> projectIds
) {
}
