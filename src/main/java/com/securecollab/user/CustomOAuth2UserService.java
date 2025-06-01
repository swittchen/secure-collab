package com.securecollab.user;

import com.securecollab.auth.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final Logger logger = LogManager.getLogger(CustomOAuth2UserService.class);

    private final OidcUserService oidcUserService = new OidcUserService();
    private final DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User;

        boolean isOidc = "openid".equals(userRequest.getClientRegistration().getScopes().stream()
                .filter(scope -> scope.contains("openid")).findFirst().orElse(null));

        if (userRequest instanceof OidcUserRequest oidcRequest) {
            OidcUser oidcUser = oidcUserService.loadUser(oidcRequest);
            oAuth2User = wrapUser(oidcUser, userRequest); // Google / OIDC
        } else {
            oAuth2User = defaultOAuth2UserService.loadUser(userRequest); // GitHub, etc.
            oAuth2User = wrapUser(oAuth2User, userRequest);
        }
        return oAuth2User;
    }

    private OAuth2User wrapUser(OAuth2User delegate, OAuth2UserRequest userRequest) {
        String email = delegate.getAttribute("email");
        String name = delegate.getAttribute("name");

        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        AuthProvider provider = AuthProvider.valueOf(
                userRequest.getClientRegistration().getRegistrationId().toUpperCase()
        );

        User user = userRepository.findByEmail(email)
                .map(existing -> updateExistingUser(existing, name, provider))
                .orElseGet(() -> registerNewUser(email, name, provider));

        return new SecureCollabOAuth2User(user, delegate.getAttributes());
    }

    private User updateExistingUser(User user, String name, AuthProvider provider) {
        if (user.getAuthProvider() != provider) {
            throw new OAuth2AuthenticationException("Use original provider: " + user.getAuthProvider());
        }
        user.setFullName(name);
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    private User registerNewUser(String email, String name, AuthProvider provider) {
        User user = new User();
        user.setEmail(email);
        user.setFullName(name);
        user.setAuthProvider(provider);
        user.setPassword(""); // нет пароля
        user.setRole(UserRole.VIEWER);
        return userRepository.save(user);
    }
}