package com.securecollab.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        if (email == null) {
            throw new RuntimeException("OAuth2 provider did not return email");
        }

        userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setPassword("");
            newUser.setRole(UserRole.VIEWER);
            return userRepository.save(newUser);
        });

        return new DefaultOAuth2User(
                Collections.singleton(() -> "ROLE_VIEWER"),
                oAuth2User.getAttributes(),
                "email"
        );
    }
}
