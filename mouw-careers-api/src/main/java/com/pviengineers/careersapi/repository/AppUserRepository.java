package com.pviengineers.careersapi.repository;

import com.pviengineers.careersapi.model.AppUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findByUsernameIgnoreCase(String username);

    Optional<AppUser> findByEmailIgnoreCase(String email);

    default Optional<AppUser> findByUsernameOrEmailIgnoreCase(String usernameOrEmail) {
        return findByUsernameIgnoreCase(usernameOrEmail)
                .or(() -> findByEmailIgnoreCase(usernameOrEmail));
    }
}
