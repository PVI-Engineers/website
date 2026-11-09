package com.mouwengineering.careersapi.config;

import com.mouwengineering.careersapi.model.AppUser;
import com.mouwengineering.careersapi.model.UserRole;
import com.mouwengineering.careersapi.repository.AppUserRepository;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserBootstrapConfig {

    @Bean
    CommandLineRunner bootstrapUsers(
            AppUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap.admin.username:admin@mouw.local}") String adminUsername,
            @Value("${app.bootstrap.admin.password:Admin@123}") String adminPassword,
            @Value("${app.bootstrap.admin.full-name:MOUW Admin}") String adminFullName,
            @Value("${app.bootstrap.admin.email:admin@mouwengineering.com}") String adminEmail,
            @Value("${app.bootstrap.hr.username:hr@mouw.local}") String hrUsername,
            @Value("${app.bootstrap.hr.password:Hr@123}") String hrPassword,
            @Value("${app.bootstrap.hr.full-name:MOUW HR}") String hrFullName,
            @Value("${app.bootstrap.hr.email:hr@mouwengineering.com}") String hrEmail,
            @Value("${app.bootstrap.employee.username:employee@mouw.local}") String employeeUsername,
            @Value("${app.bootstrap.employee.password:Employee@123}") String employeePassword,
            @Value("${app.bootstrap.employee.full-name:MOUW Employee}") String employeeFullName,
            @Value("${app.bootstrap.employee.email:employee@mouwengineering.com}") String employeeEmail
    ) {
        return args -> {
            createOrUpdateBootstrapUser(
                    userRepository,
                    passwordEncoder,
                    adminUsername,
                    adminPassword,
                    adminFullName,
                    adminEmail,
                    Set.of(UserRole.ADMIN, UserRole.HR, UserRole.EMPLOYEE)
            );
            createOrUpdateBootstrapUser(
                    userRepository,
                    passwordEncoder,
                    hrUsername,
                    hrPassword,
                    hrFullName,
                    hrEmail,
                    Set.of(UserRole.HR)
            );
            createOrUpdateBootstrapUser(
                    userRepository,
                    passwordEncoder,
                    employeeUsername,
                    employeePassword,
                    employeeFullName,
                    employeeEmail,
                    Set.of(UserRole.EMPLOYEE)
            );
        };
    }

    private void createOrUpdateBootstrapUser(
            AppUserRepository repository,
            PasswordEncoder encoder,
            String username,
            String rawPassword,
            String fullName,
            String email,
            Set<UserRole> roles
    ) {
        AppUser user = repository.findByUsernameIgnoreCase(username).orElseGet(AppUser::new);
        if (user.getId() == null) {
            user.setUsername(username);
        }
        user.setPasswordHash(encoder.encode(rawPassword));
        user.setFullName(fullName);
        user.setEmail(email);
        user.setRoles(roles);
        repository.save(user);
    }
}
