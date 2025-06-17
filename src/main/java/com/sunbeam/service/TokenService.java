package com.sunbeam.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.sunbeam.security.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TokenService {

	private final int ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60; // for 1 Hour minutes

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserDetailsService userDetailsService;

	public boolean isTokenValid(String token) {
		log.info("Validating token");
		boolean isValid = token != null && jwtUtil.validateToken(token);
		log.info("Token validation result: {}", isValid);
		return isValid;
	}

	public String getTokenFromCookies(HttpServletRequest request, String cookieName) {
		log.info("Retrieving token from cookies: {}", cookieName);
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			log.warn("No cookies found in the request");
			return null;
		}

		String token = Arrays.stream(cookies).filter(cookie -> cookieName.equals(cookie.getName()))
				.map(Cookie::getValue).findFirst().orElse(null);
		log.info("Retrieved token: {}", token != null ? "[HIDDEN]" : "null");
		return token;
	}

	public void refreshTokenIfNeeded(HttpServletRequest request, HttpServletResponse response) {
		log.info("Checking if refresh token is valid");
		String refreshToken = getTokenFromCookies(request, "refresh_token");

		if (refreshToken != null && jwtUtil.validateToken(refreshToken)) {
			log.info("Valid refresh token found, generating new access token");
			String userEmail = jwtUtil.extractEmail(refreshToken);
			String userRole = jwtUtil.extractRole(refreshToken);

			// Generate new access token
			String newAccessToken = jwtUtil.generateAccessToken(userEmail, userRole);

			log.info("New access token generated for user: {}", userEmail);
			Cookie newAccessTokenCookie = new Cookie("access_token", newAccessToken);
			newAccessTokenCookie.setHttpOnly(true);
			newAccessTokenCookie.setSecure(true);
			newAccessTokenCookie.setPath("/");
			newAccessTokenCookie.setMaxAge(ACCESS_TOKEN_EXPIRATION);

			response.addCookie(newAccessTokenCookie);

			// Authenticate user after refreshing token
			authenticateUser(request, userEmail);
		} else {
			log.warn("No valid refresh token found");
		}
	}

	private void authenticateUser(HttpServletRequest request, String email) {
		log.info("Authenticating user: {}", email);
		UserDetails userDetails = userDetailsService.loadUserByUsername(email);
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());
		request.setAttribute("email", email);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		log.info("User {} authenticated successfully", email);
	}
}