package com.sunbeam.impl;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunbeam.models.Cart;
import com.sunbeam.models.CartItem;
import com.sunbeam.models.Product;
import com.sunbeam.models.User;
import com.sunbeam.repository.CartItemRepo;
import com.sunbeam.repository.CartRepo;
import com.sunbeam.service.CartService;
import com.sunbeam.service.ProductService;
import com.sunbeam.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CartServiceImpl implements CartService {

	@Autowired
	CartRepo cartRepo;

	@Autowired
	CartItemRepo cartItemRepo;

	@Autowired
	UserService userService;

	@Autowired
	ProductService productService;

	@Override
	public Cart getUserCart(String userEmail) {
		log.info("Fetching cart for user: {}", userEmail);
		User user = userService.getUserByEmail(userEmail);
		Cart cart = cartRepo.findByUser(user).orElseGet(() -> {
			log.info("Cart not found for user: {}. Creating a new cart.", user.getEmail());
			Cart newCart = Cart.builder().user(user).cartItems(new ArrayList<>()).build();
			return cartRepo.save(newCart);
		});
		log.info("Cart fetched successfully for user: {}", user.getEmail());
		return cart;
	}

	@Override
	public Cart updateCartItemQuantity(long productId, String userEmail, String operationType) {

		log.info("Adding product {}  to cart for user: {}", productId, userEmail);

		Cart cart = this.getUserCart(userEmail);

		Optional<CartItem> existingItem = cart.getCartItems().stream()
				.filter(item -> item.getProduct().getId().equals(productId)).findFirst();
		// TODO
		// when adding a product to cart so check the availablility first

		if (existingItem.isPresent()) {
			log.info("Product {} already exists in cart. Updating quantity.", productId);
			if (operationType.equals("ADD")) {
				existingItem.get().setQuantity(existingItem.get().getQuantity() + 1); // adding one
			} else if (operationType.equals("REMOVE")) {
				existingItem.get().setQuantity(existingItem.get().getQuantity() - 1); // removing one
				if (existingItem.get().getQuantity() == 0) {
					// remove the item from the cart
					cart.getCartItems().remove(existingItem.get());
					log.info("REMOVE THE ITEM FROM THE CART");
				}
			}
		} else {
			log.info("Product {} not found in cart. Adding new item.", productId);
			if (operationType.equals("ADD")) {
				Product product = productService.getProductById(productId);
				CartItem newItem = CartItem.builder().cart(cart).product(product).quantity(1).build();
				cart.getCartItems().add(newItem);
			}
		}
		Cart updatedCart = cartRepo.save(cart);
		log.info("Cart updated successfully for user: {}", userEmail);
		return updatedCart;
	}

	@Override
	public Cart removeProductFromCart(Long productId, String userEmail) {
		log.info("Removing product {} from cart for user {}", productId, userEmail);

		User user = userService.getUserByEmail(userEmail);
		Cart cart = this.getUserCart(userEmail);

		boolean removed = cart.getCartItems().removeIf(item -> item.getProduct().getId().equals(productId));

		if (removed) {
			log.info("Product {} removed from cart for user {}", productId, userEmail);
		} else {
			log.warn("Product {} not found in cart for user {}", productId, userEmail);
		}

		return cartRepo.save(cart);
	}

	@Override
	public void clearCart(String userEmail) {
		log.info("Clearing cart for user: {}", userEmail);
		Cart cart = this.getUserCart(userEmail);
		if (cart.getCartItems().isEmpty()) {
			log.info("Cart is already empty for user: {}", userEmail);
			return;
		}
		// first delete all from cartItem then save the cart
		cartItemRepo.deleteAll(cart.getCartItems());

		cart.getCartItems().clear();
		cartRepo.save(cart);

		log.info("Cart cleared successfully for user: {}", userEmail);
	}

}
