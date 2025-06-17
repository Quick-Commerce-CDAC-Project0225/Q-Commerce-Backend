package com.sunbeam.exceptions;

public class UserAlreadyExist extends RuntimeException {
	public UserAlreadyExist(String msg) {
		super(msg);
	}
}
