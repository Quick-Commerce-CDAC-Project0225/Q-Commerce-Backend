package com.sunbeam.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.sunbeam.dto.ResponseDTO;
import com.sunbeam.product.ProductNotFound;

@ControllerAdvice
public class GlobalExceptionHandler {

	// Handling User Not Found Exception
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ResponseDTO<String>> handleUserNotFoundException(UserNotFoundException ex) {
		ResponseDTO<String> response = ResponseDTO.<String>builder().success(false).message(ex.getMessage()).data(null)
				.build();
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	// Handling 404 - NoHandlerFoundException
	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ResponseDTO<String>> handleNotFoundException(NoHandlerFoundException ex) {
		ResponseDTO<String> response = ResponseDTO.<String>builder().success(false)
				.message("Error: Endpoint not found - " + ex.getRequestURL()).data(null).build();
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	/// Handling Validation Errors (like in requestbody different type parameters)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseDTO<Map<String, String>>> handleValidationExceptions(
			MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.put(error.getField(), error.getDefaultMessage());
		}

		ResponseDTO<Map<String, String>> response = ResponseDTO.<Map<String, String>>builder().success(false)
				.message("Validation Error").data(errors).build();

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	// Handling Access Denied
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ResponseDTO<String>> handleAccessDeniedException(AccessDeniedException ex) {
		ResponseDTO<String> response = new ResponseDTO<String>(false, "Access Denied: " + ex.getMessage(), null);
		return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<ResponseDTO<String>> handleInvalidTokenException(InvalidTokenException ex) {
		ResponseDTO<String> response = new ResponseDTO<String>(false, "Error: " + ex.getMessage(), null);
		return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(UserAlreadyExist.class)
	public ResponseEntity<ResponseDTO<String>> handleUserAlreadyExistException(UserAlreadyExist ex) {
		ResponseDTO<String> response = ResponseDTO.<String>builder().success(false).message(ex.getMessage()).data(null)
				.build();
		return new ResponseEntity<>(response, HttpStatus.CONFLICT);
	}

	/// product exceptions

	@ExceptionHandler(ProductNotFound.class)
	public ResponseEntity<ResponseDTO<String>> handleProductNotFoundException(ProductNotFound ex) {
		ResponseDTO<String> response = new ResponseDTO<String>(false, "Error: " + ex.getMessage(), null);
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	// Global
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ResponseDTO<String>> handleGenericException(Exception ex) {
		ResponseDTO<String> response = new ResponseDTO<String>(false, "Error: " + ex.getMessage(), null);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
