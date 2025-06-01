package com.securecollab.security;

import com.securecollab.security.jwt.JwtUtils;
import com.securecollab.user.SecureCollabOAuth2User;
import com.securecollab.user.SecureCollabOidcUser;
import com.securecollab.user.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtils tokenProvider;
    private final Logger logger = LogManager.getLogger(OAuth2AuthenticationSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        logger.info("OAuth2AuthenticationSuccessHandler.onAuthenticationSuccess()-> authentication.getPrincipal() = {} ", authentication.getPrincipal());

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        logger.info("OAUTH USER -> {}", oauthUser.toString());

        User user;
        if (oauthUser instanceof SecureCollabOAuth2User scUser) {
            user = scUser.getUser();
        } else if (oauthUser instanceof SecureCollabOidcUser scOidc) {
            user = scOidc.getUser();
        } else {
            throw new IllegalStateException("Expected SecureCollabOAuth2User but got: " + oauthUser.getClass());
        }

        String accessToken = tokenProvider.generateAccessToken(user);
        String refreshToken = tokenProvider.generateRefreshToken(user);

        String redirectUrl = UriComponentsBuilder
                .fromUriString("http://localhost:5173/oauth2/success")
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();

        response.sendRedirect(redirectUrl);

    }
}
