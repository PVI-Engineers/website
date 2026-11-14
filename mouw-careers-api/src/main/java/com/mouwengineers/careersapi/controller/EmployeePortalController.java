package com.mouwengineering.careersapi.controller;

import com.mouwengineering.careersapi.dto.portal.EmployeeDashboardResponse;
import com.mouwengineering.careersapi.dto.portal.EmployeeResponse;
import com.mouwengineering.careersapi.dto.portal.ProfileUpdateRequest;
import com.mouwengineering.careersapi.dto.portal.TimesheetResponse;
import com.mouwengineering.careersapi.dto.portal.TimesheetSubmitRequest;
import com.mouwengineering.careersapi.service.EmployeePortalService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employee")
public class EmployeePortalController {

    private final EmployeePortalService employeePortalService;

    public EmployeePortalController(EmployeePortalService employeePortalService) {
        this.employeePortalService = employeePortalService;
    }

    @GetMapping("/home")
    public Map<String, String> home(Authentication authentication) {
        return Map.of(
                "message", "Employee portal login successful.",
                "username", authentication.getName()
        );
    }

    @GetMapping("/dashboard")
    public EmployeeDashboardResponse dashboard(Authentication authentication) {
        return employeePortalService.dashboard(authentication);
    }

    @PutMapping("/profile")
    public EmployeeResponse updateProfile(
            Authentication authentication,
            @Valid @RequestBody ProfileUpdateRequest request
    ) {
        return employeePortalService.updateProfile(authentication, request);
    }

    @PostMapping("/timesheets")
    @ResponseStatus(HttpStatus.CREATED)
    public TimesheetResponse submitTimesheet(
            Authentication authentication,
            @Valid @RequestBody TimesheetSubmitRequest request
    ) {
        return employeePortalService.submit(authentication, request);
    }
}
