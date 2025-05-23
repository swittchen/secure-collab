package com.securecollab.auth;

import com.securecollab.security.jwt.JwtUtils;
import com.securecollab.security.jwt.RefreshTokenService;
import com.securecollab.user.User;
import com.securecollab.user.UserRepository;
import com.securecollab.user.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Registration, login, refresh, logout")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    @Operation(summary = "User registration")
    @ApiResponse(responseCode = "200", description = "Registration succeed")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already used");
        }
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.VIEWER);
        userRepository.save(user);
        return ResponseEntity.ok("Registered successfully");
    }

    @Operation(summary = "User authentication")
    @ApiResponse(responseCode = "200", description = "Successfully login, returns access/refresh tokens")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        User user = (User) auth.getPrincipal();
        String access = jwtUtils.generateAccessToken(user);
        String refresh = refreshTokenService.createAndStoreToken(user.getEmail());
        return ResponseEntity.ok(Map.of("accessToken", access, "refreshToken", refresh));
    }

    @Operation(summary = "Refreshing of access tokens upon refresh token")
    @ApiResponse(responseCode = "200", description = "New access and refresh tokens")
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestBody Map<String, String> request) {
        String oldToken = request.get("refreshToken");
        return refreshTokenService.getEmailByToken(oldToken)
                .map(email -> {
                    User user = userRepository.findByEmail(email).orElseThrow();
                    String newAccess = jwtUtils.generateAccessToken(user);
                    String newRefresh = refreshTokenService.rotateToken(oldToken);
                    return ResponseEntity.ok(Map.of("accessToken", newAccess, "refreshToken", newRefresh));
                })
                .orElseGet(() -> ResponseEntity.status(401).body(
                        Map.of("error: ", "Invalid refresh token")
                ));
    }

    @Operation(summary = "Exit (invalidation of refresh tokens)")
    @ApiResponse(responseCode = "200", description = "Token deleted")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        refreshTokenService.deleteToken(refreshToken);
        return ResponseEntity.ok("Logged out");
    }
}

record RegisterRequest(String email, String password) {
}

record LoginRequest(String email, String password) {
}