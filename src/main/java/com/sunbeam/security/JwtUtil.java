package com.sunbeam.security;

import java.security.Key;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

	@Value("${jwt.secret}")
	private String SECRET_KEY;

	private final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 15; // 15 minutes
	private final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24; // 24 hours

	private Key getSigningKey() {
		return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
	}

	public String generateAccessToken(String email, String role) {
		logger.info("Generating access token for email: {}", email);
		return buildToken(email, role, ACCESS_TOKEN_EXPIRATION);
	}

	public String generateRefreshToken(String email, String role) {
		logger.info("Generating refresh token for email: {}", email);
		return buildToken(email, role, REFRESH_TOKEN_EXPIRATION);
	}

	private String buildToken(String email, String role, long expirationTime) {
		return Jwts.builder().setSubject(email).claim("role", role).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + expirationTime))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
			logger.info("Token is valid.");
			return true;
		} catch (ExpiredJwtException e) {
			logger.warn("Token has expired.");
		} catch (JwtException e) {
			logger.error("Invalid token: {}", e.getMessage());
		}
		return false;
	}

	public String extractEmail(String token) {
		String email = extractClaims(token).getSubject();
		logger.info("Extracted email from token: {}", email);
		return email;
	}

	public String extractRole(String token) {
		String role = extractClaims(token).get("role", String.class);
		logger.info("Extracted role from token: {}", role);
		return role;
	}

	public boolean isTokenExpired(String token) {
		try {
			boolean expired = extractClaims(token).getExpiration().before(new Date());
			logger.info("Token expired: {}", expired);
			return expired;
		} catch (ExpiredJwtException e) {
			logger.warn("Token has expired.");
			return true;
		}
	}

	private Claims extractClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
	}
}
