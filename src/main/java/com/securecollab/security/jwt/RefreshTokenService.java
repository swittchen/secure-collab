package com.securecollab.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(7);
    private static final String PREFIX = "refresh:";

    private final ValueOperations<String, String> ops;

    public RefreshTokenService(RedisTemplate<String, String> redisTemplate) {
        this.ops = redisTemplate.opsForValue();
    }

    public String createAndStoreToken(String userEmail) {
        String refreshToken = UUID.randomUUID().toString();
        ops.set(PREFIX + refreshToken, userEmail, REFRESH_TOKEN_TTL);
        return refreshToken;
    }

    public Optional<String> getEmailByToken(String refreshToken) {
        String email = ops.get(PREFIX + refreshToken);
        return Optional.ofNullable(email);
    }

    public void deleteToken(String refreshToken) {
        ops.getOperations().delete(PREFIX + refreshToken);
    }

    public String rotateToken(String oldToken) {
        Optional<String> emailOpt = getEmailByToken(oldToken);
        if (emailOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }
        deleteToken(oldToken);
        return createAndStoreToken(emailOpt.get());
    }

    public void deleteAllTokensForUser(String userEmail) {

    }
}
