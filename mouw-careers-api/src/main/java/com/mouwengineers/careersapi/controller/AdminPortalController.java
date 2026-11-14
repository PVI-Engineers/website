package com.mouwengineering.careersapi.controller;

import com.mouwengineering.careersapi.dto.admin.InternalUserResponse;
import com.mouwengineering.careersapi.dto.portal.EmployeeResponse;
import com.mouwengineering.careersapi.dto.portal.EmployeeWriteRequest;
import com.mouwengineering.careersapi.dto.portal.ProjectRequest;
import com.mouwengineering.careersapi.dto.portal.ProjectResponse;
import com.mouwengineering.careersapi.dto.portal.TimesheetResponse;
import com.mouwengineering.careersapi.dto.portal.TimesheetReviewRequest;
import com.mouwengineering.careersapi.repository.AppUserRepository;
import com.mouwengineering.careersapi.service.WorkforceAdminService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminPortalController {

    private final AppUserRepository appUserRepository;
    private final WorkforceAdminService workforceAdminService;

    public AdminPortalController(AppUserRepository appUserRepository, WorkforceAdminService workforceAdminService) {
        this.appUserRepository = appUserRepository;
        this.workforceAdminService = workforceAdminService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<InternalUserResponse>> getUsers() {
        List<InternalUserResponse> payload = appUserRepository.findAll().stream()
                .map(user -> new InternalUserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()),
                        user.getCreatedAt()
                ))
                .toList();

        return ResponseEntity.ok(payload);
    }

    @GetMapping("/projects")
    public List<ProjectResponse> getProjects() {
        return workforceAdminService.listProjects();
    }

    @PostMapping("/projects")
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse createProject(@Valid @RequestBody ProjectRequest request) {
        return workforceAdminService.createProject(request);
    }

    @PutMapping("/projects/{id}")
    public ProjectResponse updateProject(@PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
        return workforceAdminService.updateProject(id, request);
    }

    @GetMapping("/employees")
    public List<EmployeeResponse> getEmployees() {
        return workforceAdminService.listEmployees();
    }

    @PostMapping("/employees")
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponse createEmployee(@Valid @RequestBody EmployeeWriteRequest request) {
        return workforceAdminService.createEmployee(request);
    }

    @PutMapping("/employees/{id}")
    public EmployeeResponse updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeWriteRequest request) {
        return workforceAdminService.updateEmployee(id, request);
    }

    @GetMapping("/timesheets")
    public List<TimesheetResponse> getTimesheets() {
        return workforceAdminService.listTimesheets();
    }

    @PostMapping("/timesheets/{id}/review")
    public TimesheetResponse reviewTimesheet(
            @PathVariable Long id,
            @Valid @RequestBody TimesheetReviewRequest request,
            Authentication authentication
    ) {
        return workforceAdminService.reviewTimesheet(id, request, authentication);
    }
}
