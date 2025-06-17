package com.sunbeam.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sunbeam.dto.ResponseDTO;
import com.sunbeam.dto.user.UserDTO;
import com.sunbeam.models.User;
import com.sunbeam.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/v1/users")
public class UserController {

	@Autowired
	private UserService service;

	@GetMapping("/{id}")
	protected ResponseEntity<ResponseDTO<UserDTO>> getUserById(@RequestParam Long id) {
		log.info("Fetching user with ID: {}", id);
		User user = service.getUserById(id);
		ResponseDTO<UserDTO> response = new ResponseDTO<>(true, "success", new UserDTO(user));
		log.info("User fetched successfully: {}", user.getId());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
