package com.mouwengineering.careersapi.dto.portal;

import java.util.List;

public record EmployeeResponse(
        Long id,
        String employeeCode,
        String fullName,
        String email,
        String username,
        String designation,
        String department,
        String phone,
        List<ProjectResponse> projects
) {
}
