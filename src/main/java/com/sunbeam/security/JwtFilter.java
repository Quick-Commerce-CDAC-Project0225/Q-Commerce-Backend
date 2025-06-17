package com.sunbeam.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sunbeam.service.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	@Autowired
	private TokenService tokenService;
	@Autowired
	private final UserDetailsService userDetailsService;
	@Autowired
	JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String token = tokenService.getTokenFromCookies(request, "access_token");

		if (token != null) {
			if (!tokenService.isTokenValid(token)) {
				log.warn("JWT Token expired or invalid for request: {}", request.getRequestURI());
				tokenService.refreshTokenIfNeeded(request, response);
			} else {
				authenticateUser(request, token);
			}
		}

		filterChain.doFilter(request, response);
	}

	private void authenticateUser(HttpServletRequest request, String token) {
		String email = jwtUtil.extractEmail(token);
		UserDetails userDetails = userDetailsService.loadUserByUsername(email);
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());
		log.info("authorities : " + userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		log.info("setting requst.email : {}", email);
		request.setAttribute("email", email);
		log.info("User authenticated: {}", email);
	}
}
