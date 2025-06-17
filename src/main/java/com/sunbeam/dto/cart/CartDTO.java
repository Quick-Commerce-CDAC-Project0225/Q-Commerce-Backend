package com.sunbeam.dto.cart;

import java.util.List;
import java.util.stream.Collectors;

import com.sunbeam.models.Cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDTO {

	private Long cartId;
	private Long userId;
	private List<CartItemDTO> cartItems;
	private Double totalPrice; // total cart price adding all products

	public CartDTO(Cart cart) {
		this.cartId = cart.getId();
		this.userId = cart.getUser().getId();
		this.cartItems = cart.getCartItems().stream().map(CartItemDTO::new).collect(Collectors.toList());
		this.totalPrice = cartItems.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum(); // calculate
																												// total
																												// price
	}
}
