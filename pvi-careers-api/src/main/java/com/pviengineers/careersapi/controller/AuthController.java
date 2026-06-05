package com.pviengineers.careersapi.controller;

import com.pviengineers.careersapi.dto.auth.LoginRequest;
import com.pviengineers.careersapi.dto.auth.LoginResponse;
import com.pviengineers.careersapi.dto.auth.UserProfileResponse;
import com.pviengineers.careersapi.model.AppUser;
import com.pviengineers.careersapi.repository.AppUserRepository;
import com.pviengineers.careersapi.security.JwtService;
import jakarta.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final AppUserRepository appUserRepository;
    private final JwtService jwtService;

    public AuthController(
            AuthenticationManager authenticationManager,
            AppUserRepository appUserRepository,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.appUserRepository = appUserRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String principal = request.username().trim();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(principal, request.password())
            );
        } catch (AuthenticationException ex) {
            LOGGER.warn("Authentication failed for principal={}", principal);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password.");
        }

        AppUser user = findUserOrThrow(principal);
        String token = jwtService.generateToken(user);
        Set<String> roles = user.getRoles().stream().map(Enum::name).collect(Collectors.toSet());

        return ResponseEntity.ok(new LoginResponse(
                token,
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                roles
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> me(Authentication authentication) {
        AppUser user = findUserOrThrow(authentication.getName());
        Set<String> roles = user.getRoles().stream().map(Enum::name).collect(Collectors.toSet());

        return ResponseEntity.ok(new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                roles
        ));
    }

    private AppUser findUserOrThrow(String username) {
        return appUserRepository.findByUsernameOrEmailIgnoreCase(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
    }
}
