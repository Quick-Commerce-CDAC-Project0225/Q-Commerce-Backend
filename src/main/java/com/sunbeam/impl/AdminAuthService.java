package com.sunbeam.impl;

import com.sunbeam.dto.user.UserLoginDTO;
import com.sunbeam.dto.user.UserRegisterDTO;
import com.sunbeam.models.User;
import com.sunbeam.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AdminAuthService implements AuthService {

	@Override
	public User login(UserLoginDTO userData, HttpServletResponse response) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User register(UserRegisterDTO userData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean logout(HttpServletRequest request) {

		return false;
	}

	@Override
	public User refreshTokens(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		return null;
	}

}
