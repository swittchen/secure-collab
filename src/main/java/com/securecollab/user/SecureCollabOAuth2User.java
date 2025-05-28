package com.securecollab.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class SecureCollabOAuth2User implements OAuth2User {

    private final User user;
    private final Map<String, Object> attributes;

    public SecureCollabOAuth2User(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities();
    }

    @Override
    public String getName() {
        return user.getEmail();
    }

    public User getUser() {
        return user;
    }
}
