package com.sunbeam.service;

import com.sunbeam.dto.user.UserLoginDTO;
import com.sunbeam.dto.user.UserRegisterDTO;
import com.sunbeam.models.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
	User login(UserLoginDTO userData, HttpServletResponse response);

	User register(UserRegisterDTO userData);

	boolean logout(HttpServletRequest request);

	User refreshTokens(HttpServletRequest request, HttpServletResponse response);
}
