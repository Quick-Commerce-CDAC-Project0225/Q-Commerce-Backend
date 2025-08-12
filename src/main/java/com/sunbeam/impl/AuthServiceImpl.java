package com.sunbeam.impl;

import java.time.Duration;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sunbeam.dto.user.UserLoginDTO;
import com.sunbeam.dto.user.UserRegisterDTO;
import com.sunbeam.exceptions.InvalidTokenException;
import com.sunbeam.exceptions.UserAlreadyExist;
import com.sunbeam.exceptions.UserNotFoundException;
import com.sunbeam.models.User;
import com.sunbeam.repository.UserRepo;
import com.sunbeam.security.JwtUtil;
import com.sunbeam.service.AuthService;
import com.sunbeam.service.TokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    // ✅ Max-Age must be in SECONDS, not milliseconds
    private static final int ACCESS_TOKEN_MAX_AGE_SEC  = 60 * 60;          // 1 hour
    private static final int REFRESH_TOKEN_MAX_AGE_SEC = 60 * 60 * 24 * 7; // 7 days

    // Running on HTTP (IP on AWS), so cookies cannot be Secure yet
    private static final boolean SECURE   = false; // set true after you enable HTTPS
    private static final String  SAME_SITE = "Lax"; // switch to "None" only when SECURE = true and on HTTPS

    @Autowired private UserRepo userRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private TokenService tokenService;

    @Override
    public User login(UserLoginDTO userData, HttpServletResponse response) {
        log.info("Entering login method for email: {}", userData.getEmail());

        String email = userData.getEmail();
        String password = userData.getPassword();

        Optional<User> userOpt = userRepo.findByEmailAndEnabledTrue(email);
        if (userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPassword())) {
            log.warn("Invalid login attempt for email: {}", email);
            throw new UserNotFoundException("Invalid credentials");
        }

        // Issue tokens
        String accessToken  = jwtUtil.generateAccessToken(email, String.valueOf(userOpt.get().getRole()));
        String refreshToken = jwtUtil.generateRefreshToken(email, String.valueOf(userOpt.get().getRole()));

        // ✅ Write cookies with correct attributes
        writeCookie(response, "access_token",  accessToken,  ACCESS_TOKEN_MAX_AGE_SEC);
        writeCookie(response, "refresh_token", refreshToken, REFRESH_TOKEN_MAX_AGE_SEC);

        // (Optional) If you really want to echo tokens in headers, set them on the response:
        // response.setHeader("Authorization", "Bearer " + accessToken);
        // response.setHeader("X-Refresh-Token", refreshToken);

        log.info("User logged in successfully: {}", email);
        return userOpt.get();
    }

    @Override
    public User register(UserRegisterDTO userData) {
        log.info("Entering register method for email: {}", userData.getEmail());

        String email = userData.getEmail();

        if (userRepo.findByEmailAndEnabledTrue(email).isPresent()) {
            log.warn("User with email {} already exists", email);
            throw new UserAlreadyExist("User with email: " + email + " already exists. Please Login.");
        }

        User newUser = User.builder()
                .name(userData.getName())
                .email(email)
                .password(passwordEncoder.encode(userData.getPassword()))
                .phone(userData.getPhone())
                .role(userData.getRole())
                .enabled(true)
                .build();

        User savedUser = userRepo.save(newUser);
        log.info("User registered successfully: {}", savedUser.getEmail());
        return savedUser;
    }

    @Override
    public boolean logout(HttpServletRequest request) {
        log.info("Entering logout");
        // Note: cookie deletion is done in the CONTROLLER by writing deletion cookies on the response.
        // If you ever change the interface to accept HttpServletResponse, call deleteCookie(...) here.
        log.info("Exit logout");
        return true;
    }

    @Override
    public User refreshTokens(HttpServletRequest request, HttpServletResponse response) {
        log.info("Entering refreshTokens");

        String refreshToken = tokenService.getTokenFromCookies(request, "refresh_token");
        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            log.warn("Invalid or missing refresh token");
            throw new InvalidTokenException("Invalid refresh token");
        }

        String email = jwtUtil.extractEmail(refreshToken);
        User user = userRepo.findByEmailAndEnabledTrue(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String newAccessToken  = jwtUtil.generateAccessToken(email, String.valueOf(user.getRole()));
        String newRefreshToken = jwtUtil.generateRefreshToken(email, String.valueOf(user.getRole()));

        // ✅ overwrite cookies
        writeCookie(response, "access_token",  newAccessToken,  ACCESS_TOKEN_MAX_AGE_SEC);
        writeCookie(response, "refresh_token", newRefreshToken, REFRESH_TOKEN_MAX_AGE_SEC);

        log.info("Tokens refreshed successfully for user: {}", email);
        return user;
    }

    /* ---------- helpers ---------- */

    private void writeCookie(HttpServletResponse res, String name, String value, int maxAgeSeconds) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(SECURE)              // false on HTTP; true only on HTTPS
                .sameSite(SAME_SITE)         // "Lax" on same-site HTTP; use "None" only with SECURE=true
                .path("/")
                .maxAge(Duration.ofSeconds(maxAgeSeconds))
                .build();
        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    // If you later want to delete cookies from here:
    @SuppressWarnings("unused")
    private void deleteCookie(HttpServletResponse res, String name) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(SECURE)
                .sameSite(SAME_SITE)
                .path("/")
                .maxAge(Duration.ZERO) // delete
                .build();
        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
