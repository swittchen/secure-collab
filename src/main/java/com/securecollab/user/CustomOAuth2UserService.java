package com.securecollab.user;

import com.securecollab.auth.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // "google" или "github"
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = getEmailFromAttributes(attributes, registrationId);

        // Находим или создаём пользователя
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setAuthProvider(AuthProvider.valueOf(registrationId.toUpperCase()));
            newUser.setRole(UserRole.VIEWER); // по умолчанию
            return userRepository.save(newUser);
        });

        return new SecureCollabOAuth2User(user, attributes);
    }

    private String getEmailFromAttributes(Map<String, Object> attributes, String provider) {
        if (provider.equals("google")) {
            return (String) attributes.get("email");
        } else if (provider.equals("github")) {
            return (String) attributes.get("email");
        }
        throw new IllegalArgumentException("Unsupported provider: " + provider);
    }
}
