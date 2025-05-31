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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
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
        user.setFullName(request.fullName());
        user.setPassword(passwordEncoder.encode(request.password()));
//        UserRole role = request.role() != null ? request.role() : UserRole.VIEWER;
//        user.setRole(role);
        //admin will be set manually in db
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
        if (user.getEmail() == null) {
            throw new RuntimeException("user.getEmail() is null");
        }
        log.info("Authenticated user email: {}", user.getEmail()); // ✅ безопасно

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

    @Operation(summary = "Get current authenticated user")
    @GetMapping("/me")
    public ResponseEntity<?> getMe(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return ResponseEntity.ok(Map.of(
                "email", user.getEmail(),
                "fullName", user.getFullName(),
                "role", user.getRole()
        ));
    }
}

record RegisterRequest(String email, String password, String fullName, UserRole role) {
}

record LoginRequest(String email, String password) {
}