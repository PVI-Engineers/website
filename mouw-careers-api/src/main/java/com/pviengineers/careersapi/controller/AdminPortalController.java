package com.pviengineers.careersapi.controller;

import com.pviengineers.careersapi.dto.admin.InternalUserResponse;
import com.pviengineers.careersapi.repository.AppUserRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminPortalController {

    private final AppUserRepository appUserRepository;

    public AdminPortalController(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
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
}
