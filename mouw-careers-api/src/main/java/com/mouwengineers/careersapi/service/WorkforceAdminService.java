package com.mouwengineering.careersapi.service;

import com.mouwengineering.careersapi.dto.portal.EmployeeResponse;
import com.mouwengineering.careersapi.dto.portal.EmployeeWriteRequest;
import com.mouwengineering.careersapi.dto.portal.ProjectRequest;
import com.mouwengineering.careersapi.dto.portal.ProjectResponse;
import com.mouwengineering.careersapi.dto.portal.TimesheetResponse;
import com.mouwengineering.careersapi.dto.portal.TimesheetReviewRequest;
import com.mouwengineering.careersapi.model.AppUser;
import com.mouwengineering.careersapi.model.Project;
import com.mouwengineering.careersapi.model.TimesheetEntry;
import com.mouwengineering.careersapi.model.TimesheetStatus;
import com.mouwengineering.careersapi.model.UserRole;
import com.mouwengineering.careersapi.repository.AppUserRepository;
import com.mouwengineering.careersapi.repository.ProjectRepository;
import com.mouwengineering.careersapi.repository.TimesheetEntryRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WorkforceAdminService {

    private final AppUserRepository appUserRepository;
    private final ProjectRepository projectRepository;
    private final TimesheetEntryRepository timesheetEntryRepository;
    private final PasswordEncoder passwordEncoder;

    public WorkforceAdminService(
            AppUserRepository appUserRepository,
            ProjectRepository projectRepository,
            TimesheetEntryRepository timesheetEntryRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.appUserRepository = appUserRepository;
        this.projectRepository = projectRepository;
        this.timesheetEntryRepository = timesheetEntryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> listProjects() {
        return projectRepository.findAllByOrderByNameAsc().stream().map(this::toProject).toList();
    }

    @Transactional
    public ProjectResponse createProject(ProjectRequest request) {
        String code = normalizeCode(request.code());
        if (projectRepository.findByCodeIgnoreCase(code).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A project with that code already exists.");
        }
        Project project = new Project();
        applyProject(project, request, code, true);
        return toProject(projectRepository.save(project));
    }

    @Transactional
    public ProjectResponse updateProject(Long id, ProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found."));
        String code = normalizeCode(request.code());
        projectRepository.findByCodeIgnoreCase(code)
                .filter(existing -> !existing.getId().equals(project.getId()))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "A project with that code already exists.");
                });
        boolean active = request.active() == null ? project.isActive() : request.active();
        applyProject(project, request, code, active);
        return toProject(project);
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> listEmployees() {
        return appUserRepository.findAll().stream()
                .filter(this::isManagedEmployee)
                .sorted(Comparator.comparing(AppUser::getFullName, String.CASE_INSENSITIVE_ORDER))
                .map(this::toEmployee)
                .toList();
    }

    @Transactional
    public EmployeeResponse createEmployee(EmployeeWriteRequest request) {
        String password = request.password() == null ? "" : request.password().trim();
        if (password.length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 8 characters.");
        }
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        String employeeCode = request.employeeCode().trim();
        if (appUserRepository.existsByEmailIgnoreCase(email) || appUserRepository.findByUsernameIgnoreCase(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with that email already exists.");
        }
        if (appUserRepository.existsByEmployeeCodeIgnoreCase(employeeCode)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "That employee ID is already in use.");
        }

        AppUser employee = new AppUser();
        employee.setUsername(email);
        employee.setPasswordHash(passwordEncoder.encode(password));
        employee.setRoles(Set.of(UserRole.EMPLOYEE));
        applyEmployee(employee, request, email, employeeCode);
        return toEmployee(appUserRepository.save(employee));
    }

    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeWriteRequest request) {
        AppUser employee = appUserRepository.findById(id)
                .filter(this::isManagedEmployee)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found."));
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        String employeeCode = request.employeeCode().trim();
        if (!email.equalsIgnoreCase(employee.getEmail())) {
            boolean emailTaken = appUserRepository.findByEmailIgnoreCase(email)
                    .filter(existing -> !existing.getId().equals(employee.getId()))
                    .isPresent();
            boolean usernameTaken = appUserRepository.findByUsernameIgnoreCase(email)
                    .filter(existing -> !existing.getId().equals(employee.getId()))
                    .isPresent();
            if (emailTaken || usernameTaken) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with that email already exists.");
            }
        }
        if (employee.getEmployeeCode() == null || !employeeCode.equalsIgnoreCase(employee.getEmployeeCode())) {
            if (appUserRepository.existsByEmployeeCodeIgnoreCase(employeeCode)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "That employee ID is already in use.");
            }
        }
        String password = request.password() == null ? "" : request.password().trim();
        if (!password.isEmpty()) {
            if (password.length() < 8) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 8 characters.");
            }
            employee.setPasswordHash(passwordEncoder.encode(password));
        }
        if (employee.getUsername() != null && employee.getUsername().equalsIgnoreCase(employee.getEmail())) {
            employee.setUsername(email);
        }
        applyEmployee(employee, request, email, employeeCode);
        return toEmployee(employee);
    }

    @Transactional(readOnly = true)
    public List<TimesheetResponse> listTimesheets() {
        return timesheetEntryRepository.findAllDetailed().stream().map(this::toTimesheet).toList();
    }

    @Transactional
    public TimesheetResponse reviewTimesheet(Long id, TimesheetReviewRequest request, Authentication authentication) {
        TimesheetStatus decision = parseDecision(request.decision());
        String note = request.note() == null ? "" : request.note().trim();
        if (decision == TimesheetStatus.REJECTED && note.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Add a note when rejecting a timesheet.");
        }
        TimesheetEntry entry = timesheetEntryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Timesheet not found."));
        if (entry.getStatus() != TimesheetStatus.SUBMITTED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only submitted timesheets can be reviewed.");
        }
        AppUser reviewer = appUserRepository.findByUsernameIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Your session expired. Please login again."));
        entry.setStatus(decision);
        entry.setReviewNote(note.isEmpty() ? null : note);
        entry.setReviewedAt(LocalDateTime.now());
        entry.setReviewedBy(reviewer);
        return toTimesheet(entry);
    }

    private void applyProject(Project project, ProjectRequest request, String code, boolean active) {
        project.setName(request.name().trim());
        project.setCode(code);
        String description = request.description() == null ? "" : request.description().trim();
        project.setDescription(description.isEmpty() ? null : description);
        project.setActive(active);
    }

    private void applyEmployee(AppUser employee, EmployeeWriteRequest request, String email, String employeeCode) {
        employee.setFullName(request.fullName().trim());
        employee.setEmail(email);
        employee.setEmployeeCode(employeeCode);
        employee.setDesignation(request.designation().trim());
        employee.setDepartment(request.department().trim());
        employee.getAssignedProjects().clear();
        employee.getAssignedProjects().addAll(resolveProjects(request.projectIds()));
    }

    private List<Project> resolveProjects(List<Long> projectIds) {
        Set<Long> uniqueIds = new HashSet<>(projectIds);
        List<Project> projects = projectRepository.findAllById(uniqueIds);
        if (projects.size() != uniqueIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more selected projects do not exist.");
        }
        return projects;
    }

    private boolean isManagedEmployee(AppUser user) {
        return user.getRoles().contains(UserRole.EMPLOYEE)
                && !user.getRoles().contains(UserRole.ADMIN)
                && !user.getRoles().contains(UserRole.HR);
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase(Locale.ROOT);
    }

    private TimesheetStatus parseDecision(String decision) {
        try {
            TimesheetStatus status = TimesheetStatus.valueOf(decision.trim().toUpperCase(Locale.ROOT));
            if (status == TimesheetStatus.SUBMITTED) {
                throw new IllegalArgumentException();
            }
            return status;
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Decision must be APPROVED or REJECTED.");
        }
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

    private EmployeeResponse toEmployee(AppUser employee) {
        List<ProjectResponse> projects = employee.getAssignedProjects().stream()
                .sorted(Comparator.comparing(Project::getName, String.CASE_INSENSITIVE_ORDER))
                .map(this::toProject)
                .toList();
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
