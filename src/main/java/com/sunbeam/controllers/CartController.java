package com.sunbeam.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sunbeam.dto.ResponseDTO;
import com.sunbeam.dto.cart.CartDTO;
import com.sunbeam.models.Cart;
import com.sunbeam.service.CartService;
import com.sunbeam.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/cart")

public class CartController {

	@Autowired
	CartService cartService;
	@Autowired
	UserService userService;

	@GetMapping
	public ResponseEntity<ResponseDTO<CartDTO>> getUserCart(HttpServletRequest request) {
		String email = (String) request.getAttribute("email");
		Cart cart = cartService.getUserCart(email);
		return ResponseEntity.ok(new ResponseDTO<>(true, "Success", new CartDTO(cart)));
	}

	@PostMapping("/items")
	public ResponseEntity<ResponseDTO<CartDTO>> increaseQuantity(@RequestParam Long productId,
			HttpServletRequest request) {
		String email = (String) request.getAttribute("email");
		Cart cart = cartService.updateCartItemQuantity(productId, email, "ADD");
		return ResponseEntity.ok(new ResponseDTO<>(true, "Success", new CartDTO(cart)));
	}

	@PostMapping("/items/decrease")
	public ResponseEntity<ResponseDTO<CartDTO>> decreaseQuantity(@RequestParam Long productId,
			HttpServletRequest request) {
		String email = (String) request.getAttribute("email");

		Cart cart = cartService.updateCartItemQuantity(productId, email, "REMOVE");
		return ResponseEntity.ok(new ResponseDTO<>(true, "Success", new CartDTO(cart)));
	}

	@DeleteMapping("/items/{productId}")
	public ResponseEntity<ResponseDTO<CartDTO>> removeProductFromCart(@PathVariable Long productId,
			HttpServletRequest request) {
		String email = (String) request.getAttribute("email");
		Cart cart = cartService.removeProductFromCart(productId, email);
		return ResponseEntity.ok(new ResponseDTO<>(true, "Product removed from cart.", new CartDTO(cart)));
	}

	@DeleteMapping
	public ResponseEntity<ResponseDTO<String>> clearCart(HttpServletRequest req) {

		String userEmail = (String) req.getAttribute("email");
		cartService.clearCart(userEmail);
		return ResponseEntity.ok(new ResponseDTO<>(true, "Cart cleared successfully.", null));
	}

}
