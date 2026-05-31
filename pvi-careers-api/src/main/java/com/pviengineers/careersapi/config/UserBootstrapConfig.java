package com.pviengineers.careersapi.config;

import com.pviengineers.careersapi.model.AppUser;
import com.pviengineers.careersapi.model.UserRole;
import com.pviengineers.careersapi.repository.AppUserRepository;
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
            @Value("${app.bootstrap.admin.username:admin@pvi.local}") String adminUsername,
            @Value("${app.bootstrap.admin.password:Admin@123}") String adminPassword,
            @Value("${app.bootstrap.admin.full-name:PVI Admin}") String adminFullName,
            @Value("${app.bootstrap.admin.email:admin@pviengineers.com}") String adminEmail,
            @Value("${app.bootstrap.hr.username:hr@pvi.local}") String hrUsername,
            @Value("${app.bootstrap.hr.password:Hr@123}") String hrPassword,
            @Value("${app.bootstrap.hr.full-name:PVI HR}") String hrFullName,
            @Value("${app.bootstrap.hr.email:hr@pviengineers.com}") String hrEmail,
            @Value("${app.bootstrap.employee.username:employee@pvi.local}") String employeeUsername,
            @Value("${app.bootstrap.employee.password:Employee@123}") String employeePassword,
            @Value("${app.bootstrap.employee.full-name:PVI Employee}") String employeeFullName,
            @Value("${app.bootstrap.employee.email:employee@pviengineers.com}") String employeeEmail
    ) {
        return args -> {
            createUserIfMissing(
                    userRepository,
                    passwordEncoder,
                    adminUsername,
                    adminPassword,
                    adminFullName,
                    adminEmail,
                    Set.of(UserRole.ADMIN, UserRole.HR, UserRole.EMPLOYEE)
            );
            createUserIfMissing(
                    userRepository,
                    passwordEncoder,
                    hrUsername,
                    hrPassword,
                    hrFullName,
                    hrEmail,
                    Set.of(UserRole.HR)
            );
            createUserIfMissing(
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

    private void createUserIfMissing(
            AppUserRepository repository,
            PasswordEncoder encoder,
            String username,
            String rawPassword,
            String fullName,
            String email,
            Set<UserRole> roles
    ) {
        if (repository.findByUsername(username).isPresent()) {
            return;
        }

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPasswordHash(encoder.encode(rawPassword));
        user.setFullName(fullName);
        user.setEmail(email);
        user.setRoles(roles);
        repository.save(user);
    }
}
