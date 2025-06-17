package com.sunbeam.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

	private final int ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60; // 1 Hour
	private final int REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24; // 7 Days
	@Autowired
	private UserRepo userRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private TokenService tokenService;

	@Override
	public User login(UserLoginDTO userData, HttpServletResponse response) {
		log.info("Entering login method for email: {}", userData.getEmail());

		String email = userData.getEmail();
		String password = userData.getPassword();
		Optional<User> user = userRepo.findByEmailAndEnabledTrue(email);

		if (user.isEmpty() || !passwordEncoder.matches(password, user.get().getPassword())) {
			log.warn("Invalid login attempt for email: {}", email);
			throw new UserNotFoundException("Invalid credentials");
		}

		String accessToken = jwtUtil.generateAccessToken(email, String.valueOf(user.get().getRole()));
		String refreshToken = jwtUtil.generateRefreshToken(email, String.valueOf(user.get().getRole()));

		addCookie(response, "access_token", accessToken, ACCESS_TOKEN_EXPIRATION);
		addCookie(response, "refresh_token", refreshToken, REFRESH_TOKEN_EXPIRATION);

		log.info("User logged in successfully: {}", email);
		return user.get();
	}

	@Override
	public User register(UserRegisterDTO userData) {
		log.info("Entering register method for email: {}", userData.getEmail());

		String email = userData.getEmail();
		String password = userData.getPassword();
		Optional<User> user = userRepo.findByEmailAndEnabledTrue(email);

		if (user.isPresent()) {
			log.warn("User with email {} already exists", email);

			throw new UserAlreadyExist("User with email: " + email + " already exists. Please Login.");
		}

		User newUser = User.builder().name(userData.getName()).email(userData.getEmail())
				.password(passwordEncoder.encode(password)).role(userData.getRole()).enabled(true).build();

		User savedUser = userRepo.save(newUser);
		log.info("User registered successfully: {}", savedUser.getEmail());

		return savedUser;
	}

	@Override
	public boolean logout(HttpServletRequest request) {
		log.info("Entering logout");
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("access_token".equals(cookie.getName()) || "refresh_token".equals(cookie.getName())) {
					cookie.setMaxAge(0);
					cookie.setPath("/");
				}
			}
		}

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
		Optional<User> userOpt = userRepo.findByEmailAndEnabledTrue(email);

		if (userOpt.isEmpty()) {
			log.warn("User not found for refresh token");
			throw new UserNotFoundException("User not found");
		}

		User user = userOpt.get();
		String newAccessToken = jwtUtil.generateAccessToken(email, String.valueOf(user.getRole()));
		String newRefreshToken = jwtUtil.generateRefreshToken(email, String.valueOf(user.getRole()));

		addCookie(response, "access_token", newAccessToken, ACCESS_TOKEN_EXPIRATION);
		addCookie(response, "refresh_token", newRefreshToken, REFRESH_TOKEN_EXPIRATION);

		log.info("Tokens refreshed successfully for user: {}", email);
		return user;
	}

	private void addCookie(HttpServletResponse response, String name, String value, int expirationTime) {
		Cookie cookie = new Cookie(name, value);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setMaxAge(expirationTime);
		response.addCookie(cookie);
	}
}
