package com.securecollab.user;

import com.securecollab.auth.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository userRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser delegate = super.loadUser(userRequest);

        String email = delegate.getAttribute("email");
        String name = delegate.getAttribute("name");

        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found from OIDC provider");
        }

        AuthProvider provider = AuthProvider.valueOf(
                userRequest.getClientRegistration().getRegistrationId().toUpperCase()
        );

        User user = userRepository.findByEmail(email)
                .map(existing -> updateUser(existing, name, provider))
                .orElseGet(() -> createUser(email, name, provider));

        return new SecureCollabOidcUser(user, delegate); // обёртка — см. ниже
    }

    private User updateUser(User user, String name, AuthProvider provider) {
        if (user.getAuthProvider() != provider) {
            throw new OAuth2AuthenticationException("Use original provider: " + user.getAuthProvider());
        }
        user.setFullName(name);
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    private User createUser(String email, String name, AuthProvider provider) {
        User user = new User();
        user.setEmail(email);
        user.setFullName(name);
        user.setAuthProvider(provider);
        user.setPassword("");
        user.setRole(UserRole.VIEWER);
        return userRepository.save(user);
    }
}
