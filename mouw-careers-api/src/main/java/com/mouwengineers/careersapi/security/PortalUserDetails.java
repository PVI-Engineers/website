package com.mouwengineering.careersapi.security;

import java.time.LocalDateTime;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class PortalUserDetails implements UserDetails {

    private final UserDetails delegate;
    private final LocalDateTime credentialsUpdatedAt;

    public PortalUserDetails(UserDetails delegate, LocalDateTime credentialsUpdatedAt) {
        this.delegate = delegate;
        this.credentialsUpdatedAt = credentialsUpdatedAt;
    }

    public LocalDateTime getCredentialsUpdatedAt() {
        return credentialsUpdatedAt;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return delegate.getAuthorities();
    }

    @Override
    public String getPassword() {
        return delegate.getPassword();
    }

    @Override
    public String getUsername() {
        return delegate.getUsername();
    }
}
