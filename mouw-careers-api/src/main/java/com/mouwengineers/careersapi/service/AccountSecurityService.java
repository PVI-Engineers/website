package com.mouwengineering.careersapi.service;

import com.mouwengineering.careersapi.dto.auth.LoginResponse;
import com.mouwengineering.careersapi.dto.auth.PasswordChangeRequest;
import com.mouwengineering.careersapi.model.AppUser;
import com.mouwengineering.careersapi.repository.AppUserRepository;
import com.mouwengineering.careersapi.security.JwtService;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AccountSecurityService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AccountSecurityService(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public LoginResponse changePassword(Authentication authentication, PasswordChangeRequest request) {
        AppUser user = currentUser(authentication);
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect.");
        }
        if (request.currentPassword().equals(request.newPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose a new password that is different from the current one.");
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        user.setCredentialsUpdatedAt(LocalDateTime.now().minusSeconds(1));
        appUserRepository.save(user);

        Set<String> roles = user.getRoles().stream().map(Enum::name).collect(Collectors.toSet());
        return new LoginResponse(
                jwtService.generateToken(user),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                roles
        );
    }

    private AppUser currentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Your session expired. Please login again.");
        }
        return appUserRepository.findByUsernameIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Your session expired. Please login again."));
    }
}
