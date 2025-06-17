package com.sunbeam.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunbeam.dto.ResponseDTO;
import com.sunbeam.dto.user.UserDTO;
import com.sunbeam.dto.user.UserLoginDTO;
import com.sunbeam.dto.user.UserRegisterDTO;
import com.sunbeam.models.Role;
import com.sunbeam.models.User;
import com.sunbeam.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
//@CrossOrigin(originPatterns = "*")
public class AuthController {

	@Autowired
	private AuthService authService;

	@PostMapping("/signup")
	protected ResponseEntity<ResponseDTO<UserDTO>> register(@RequestBody UserRegisterDTO userData) {
		log.info("Entering register");
		userData.setRole(Role.ROLE_CUSTOMER);
		User newUser = authService.register(userData);
		ResponseDTO<UserDTO> response = new ResponseDTO<>(true, "User registered successfully", new UserDTO(newUser));
		log.info("Exit register");
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PostMapping("/login")
	protected ResponseEntity<ResponseDTO<UserDTO>> login(@RequestBody UserLoginDTO userData,
			HttpServletResponse response) {
		log.info("Entering login");
		User user = authService.login(userData, response);
		log.info("Exit login");
		return new ResponseEntity<>(new ResponseDTO<>(true, "User logged in successfully", new UserDTO(user)),
				HttpStatus.OK);
	}

	@PostMapping("/logout")
	protected ResponseEntity<ResponseDTO<Boolean>> logout(HttpServletRequest request, HttpServletResponse response) {
		log.info("Entering logout");
		authService.logout(request);

		// Remove cookies
		Cookie accessTokenCookie = new Cookie("access_token", null);
		accessTokenCookie.setMaxAge(0);
		accessTokenCookie.setHttpOnly(true);
		accessTokenCookie.setSecure(true);
		accessTokenCookie.setPath("/");

		Cookie refreshTokenCookie = new Cookie("refresh_token", null);
		refreshTokenCookie.setMaxAge(0);
		refreshTokenCookie.setHttpOnly(true);
		refreshTokenCookie.setSecure(true);
		refreshTokenCookie.setPath("/");

		response.addCookie(accessTokenCookie);
		response.addCookie(refreshTokenCookie);

		log.info("Exit logout");
		return new ResponseEntity<>(new ResponseDTO<>(true, "User logged out successfully", true), HttpStatus.OK);
	}

	@PostMapping("/refresh")
	protected ResponseEntity<ResponseDTO<UserDTO>> refreshTokens(HttpServletRequest request,
			HttpServletResponse response) {
		log.info("Entering refreshTokens");
		User user = authService.refreshTokens(request, response);
		log.info("Exit refreshTokens");
		return new ResponseEntity<>(new ResponseDTO<>(true, "Tokens refreshed successfully", new UserDTO(user)),
				HttpStatus.OK);
	}

}
