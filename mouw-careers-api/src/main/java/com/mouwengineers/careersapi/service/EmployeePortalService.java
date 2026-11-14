package com.mouwengineering.careersapi.service;

import com.mouwengineering.careersapi.dto.portal.ActivityResponse;
import com.mouwengineering.careersapi.dto.portal.EmployeeDashboardResponse;
import com.mouwengineering.careersapi.dto.portal.EmployeeResponse;
import com.mouwengineering.careersapi.dto.portal.HoursSummaryResponse;
import com.mouwengineering.careersapi.dto.portal.ProfileUpdateRequest;
import com.mouwengineering.careersapi.dto.portal.ProjectResponse;
import com.mouwengineering.careersapi.dto.portal.TimesheetResponse;
import com.mouwengineering.careersapi.dto.portal.TimesheetSubmitRequest;
import com.mouwengineering.careersapi.model.AppUser;
import com.mouwengineering.careersapi.model.Project;
import com.mouwengineering.careersapi.model.TimesheetEntry;
import com.mouwengineering.careersapi.model.TimesheetStatus;
import com.mouwengineering.careersapi.repository.AppUserRepository;
import com.mouwengineering.careersapi.repository.TimesheetEntryRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmployeePortalService {

    private final AppUserRepository appUserRepository;
    private final TimesheetEntryRepository timesheetEntryRepository;

    public EmployeePortalService(
            AppUserRepository appUserRepository,
            TimesheetEntryRepository timesheetEntryRepository
    ) {
        this.appUserRepository = appUserRepository;
        this.timesheetEntryRepository = timesheetEntryRepository;
    }

    @Transactional(readOnly = true)
    public EmployeeDashboardResponse dashboard(Authentication authentication) {
        AppUser employee = requireEmployee(authentication);
        List<TimesheetEntry> entries = timesheetEntryRepository.findForEmployee(employee.getId());
        List<ProjectResponse> projects = activeProjects(employee);
        return new EmployeeDashboardResponse(
                toEmployee(employee, projects),
                projects,
                summarize(entries),
                recentActivity(entries),
                entries.stream().map(this::toTimesheet).toList()
        );
    }

    @Transactional
    public TimesheetResponse submit(Authentication authentication, TimesheetSubmitRequest request) {
        AppUser employee = requireEmployee(authentication);
        if (request.workDate().isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Work date cannot be in the future.");
        }

        Project project = employee.getAssignedProjects().stream()
                .filter(item -> item.getId().equals(request.projectId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Select a project assigned to your account."
                ));
        if (!project.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "That project is no longer active.");
        }
        if (timesheetEntryRepository.existsByEmployeeIdAndProjectIdAndWorkDateAndStatusNot(
                employee.getId(),
                project.getId(),
                request.workDate(),
                TimesheetStatus.REJECTED
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A timesheet for this project and date is already submitted."
            );
        }

        TimesheetEntry entry = new TimesheetEntry();
        entry.setEmployee(employee);
        entry.setProject(project);
        entry.setWorkDate(request.workDate());
        entry.setHours(request.hours());
        entry.setWorkDescription(request.workDescription().trim());
        entry.setStatus(TimesheetStatus.SUBMITTED);
        return toTimesheet(timesheetEntryRepository.save(entry));
    }

    @Transactional
    public EmployeeResponse updateProfile(Authentication authentication, ProfileUpdateRequest request) {
        AppUser employee = requireEmployee(authentication);
        String phone = request.phone() == null ? "" : request.phone().trim();
        if (!phone.isEmpty() && !phone.matches("^[0-9+()\\-\\s]{7,40}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter a valid phone number.");
        }
        employee.setPhone(phone.isEmpty() ? null : phone);
        return toEmployee(employee, activeProjects(employee));
    }

    private AppUser requireEmployee(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Your session expired. Please login again.");
        }
        return appUserRepository.findByUsernameIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Your session expired. Please login again."));
    }

    private List<ProjectResponse> activeProjects(AppUser employee) {
        return employee.getAssignedProjects().stream()
                .filter(Project::isActive)
                .sorted(Comparator.comparing(Project::getName, String.CASE_INSENSITIVE_ORDER))
                .map(this::toProject)
                .toList();
    }

    private HoursSummaryResponse summarize(List<TimesheetEntry> entries) {
        BigDecimal pending = sum(entries, TimesheetStatus.SUBMITTED);
        BigDecimal approved = sum(entries, TimesheetStatus.APPROVED);
        BigDecimal rejected = sum(entries, TimesheetStatus.REJECTED);
        return new HoursSummaryResponse(
                pending.add(approved).add(rejected),
                pending,
                approved,
                rejected,
                entries.size()
        );
    }

    private BigDecimal sum(List<TimesheetEntry> entries, TimesheetStatus status) {
        return entries.stream()
                .filter(entry -> entry.getStatus() == status)
                .map(TimesheetEntry::getHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<ActivityResponse> recentActivity(List<TimesheetEntry> entries) {
        return entries.stream()
                .limit(8)
                .map(entry -> new ActivityResponse(
                        entry.getId(),
                        activityMessage(entry),
                        entry.getReviewedAt() != null ? entry.getReviewedAt() : entry.getSubmittedAt()
                ))
                .toList();
    }

    private String activityMessage(TimesheetEntry entry) {
        String project = entry.getProject().getName();
        return switch (entry.getStatus()) {
            case APPROVED -> "Approved " + entry.getHours() + " hours on " + project + ".";
            case REJECTED -> "Rejected " + entry.getHours() + " hours on " + project + ".";
            case SUBMITTED -> "Submitted " + entry.getHours() + " hours on " + project + ".";
        };
    }

    private EmployeeResponse toEmployee(AppUser employee, List<ProjectResponse> projects) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getEmployeeCode(),
                employee.getFullName(),
                employee.getEmail(),
                employee.getUsername(),
                employee.getDesignation(),
                employee.getDepartment(),
                employee.getPhone(),
                projects
        );
    }

    private ProjectResponse toProject(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getCode(),
                project.getDescription(),
                project.isActive()
        );
    }

    private TimesheetResponse toTimesheet(TimesheetEntry entry) {
        return new TimesheetResponse(
                entry.getId(),
                entry.getEmployee().getId(),
                entry.getEmployee().getEmployeeCode(),
                entry.getEmployee().getFullName(),
                entry.getProject().getId(),
                entry.getProject().getName(),
                entry.getProject().getCode(),
                entry.getWorkDate(),
                entry.getHours(),
                entry.getWorkDescription(),
                entry.getStatus().name(),
                entry.getSubmittedAt(),
                entry.getReviewedAt(),
                entry.getReviewNote()
        );
    }
}
