package com.pviengineers.careersapi.security;

import com.pviengineers.careersapi.model.UserRole;
import com.pviengineers.careersapi.repository.AppUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    public CustomUserDetailsService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return appUserRepository.findByUsername(username)
                .map(user -> User.withUsername(user.getUsername())
                        .password(user.getPasswordHash())
                        .authorities(user.getRoles().stream()
                                .map(UserRole::name)
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                .toList())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));
    }
}
