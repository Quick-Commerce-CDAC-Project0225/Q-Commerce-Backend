package com.sunbeam.service;

import com.sunbeam.models.Cart;

public interface CartService {
	Cart updateCartItemQuantity(long productId, String userEmail, String operationType);

	Cart removeProductFromCart(Long productId, String userEmail);

	public Cart getUserCart(String userEmail);

	public void clearCart(String userEmail);
}
