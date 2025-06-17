package com.sunbeam.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sunbeam.models.User;
import com.sunbeam.service.UserService;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

	@Autowired
	private UserService userService;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		logger.info("Attempting to load user by email: {}", email);

		User user = userService.getUserByEmail(email);

		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().toString());
		logger.info(user.getRole().toString());
		logger.info("user authority" + authority.toString());
		logger.info("User {} successfully loaded with role: {}", email, user.getRole());

		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
				List.of(authority));
	}
}
