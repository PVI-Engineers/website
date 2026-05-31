package com.pviengineers.careersapi.controller;

import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employee")
public class EmployeePortalController {

    @GetMapping("/home")
    public Map<String, String> home(Authentication authentication) {
        return Map.of(
                "message", "Employee portal login successful.",
                "username", authentication.getName()
        );
    }
}
