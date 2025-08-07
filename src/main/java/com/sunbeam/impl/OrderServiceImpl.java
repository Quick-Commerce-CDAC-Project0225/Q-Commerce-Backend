//package com.sunbeam.impl;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.sunbeam.dto.order.OrderRequestDto;
//import com.sunbeam.models.Order;
//import com.sunbeam.models.OrderItem;
//import com.sunbeam.models.OrderStatus;
//import com.sunbeam.models.Product;
//import com.sunbeam.models.User;
//import com.sunbeam.repository.OrderItemRepo;
//import com.sunbeam.repository.OrderRepo;
//import com.sunbeam.service.OrderService;
//import com.sunbeam.service.ProductService;
//import com.sunbeam.service.UserService;
//
//import jakarta.transaction.Transactional;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Service
//public class OrderServiceImpl implements OrderService {
//
//	@Autowired
//	UserService userService;
//
//	@Autowired
//	ProductService productService;
//
//	@Autowired
//	OrderRepo orderRepo;
//
//	@Autowired
//	OrderItemRepo orderItemRepo;
//
//	@Override
//	@Transactional
//	public Order createOrder(OrderRequestDto orderReq, String userEmail) {
//		log.info("Entering createOrder for user: {}", userEmail);
//
//		if (orderReq.getProducts() == null || orderReq.getProducts().isEmpty()) {
//			log.error("Order must contain at least one product.");
//			throw new IllegalArgumentException("Order must contain at least one product.");
//		}
//
//		User user = userService.getUserByEmail(userEmail);
//		log.info("Fetched user: {}", user.getEmail());
//
//		Order order = Order.builder().user(user).address(orderReq.getAddress()).type(orderReq.getType())
//				.status(OrderStatus.PENDING).build();
//
//		Order savedOrder = orderRepo.save(order);
//		log.info("Order saved with ID: {}", savedOrder.getId());
//
//		List<OrderItem> orderItems = orderReq.getProducts().stream().map(productDto -> {
//			Product product = productService.getProductById(productDto.getProductId());
//			log.info("Fetched product with ID: {}", product.getId());
//
//			OrderItem orderItem = new OrderItem();
//			orderItem.setOrder(savedOrder);
//			orderItem.setProduct(product);
//			orderItem.setQuantity(productDto.getQuantity());
//			orderItem.setPrice(product.getPrice() * productDto.getQuantity());
//
//			return orderItem;
//		}).collect(Collectors.toList());
//
//		List<OrderItem> savedItems = orderItemRepo.saveAll(orderItems);
//		log.info("Order items saved for order ID: {}", savedOrder.getId());
//
//		savedOrder.setItems(savedItems);
//		return savedOrder;
//	}
//
//	@Override
////	TODO
//	public List<Order> getAllOrders() {
//		List<Order> orders = orderRepo.findAll();
//		return orders;
//	}
//
//	@Override
//	public List<Order> getOrdersByUser(String userEmail) {
//		User user = userService.getUserByEmail(userEmail);
//		List<Order> orders = orderRepo.findAllByUser(user);
//		return orders;
//	}
//
//}

package com.sunbeam.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunbeam.dto.order.OrderRequestDto;
import com.sunbeam.models.Order;
import com.sunbeam.models.OrderItem;
import com.sunbeam.models.OrderStatus;
import com.sunbeam.models.Product;
import com.sunbeam.models.User;
import com.sunbeam.repository.OrderItemRepo;
import com.sunbeam.repository.OrderRepo;
import com.sunbeam.service.OrderService;
import com.sunbeam.service.ProductService;
import com.sunbeam.service.UserService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	UserService userService;

	@Autowired
	ProductService productService;

	@Autowired
	OrderRepo orderRepo;

	@Autowired
	OrderItemRepo orderItemRepo;

	@Override
	@Transactional
	public Order createOrder(OrderRequestDto orderReq, String userEmail) {
		log.info("Entering createOrder for user: {}", userEmail);

		if (orderReq.getProducts() == null || orderReq.getProducts().isEmpty()) {
			log.error("Order must contain at least one product.");
			throw new IllegalArgumentException("Order must contain at least one product.");
		}

		User user = userService.getUserByEmail(userEmail);
		log.info("Fetched user: {}", user.getEmail());

		Order order = Order.builder()
				.user(user)
				.address(orderReq.getAddress())
				.type(orderReq.getType())
				.status(OrderStatus.PENDING)
				.build();

		Order savedOrder = orderRepo.save(order);
		log.info("Order saved with ID: {}", savedOrder.getId());

		List<OrderItem> orderItems = orderReq.getProducts().stream().map(productDto -> {
			Product product = productService.getProductById(productDto.getProductId());
			log.info("Fetched product with ID: {}", product.getId());

			OrderItem orderItem = new OrderItem();
			orderItem.setOrder(savedOrder);
			orderItem.setProduct(product);
			orderItem.setQuantity(productDto.getQuantity());
			orderItem.setPrice(product.getPrice() * productDto.getQuantity());

			return orderItem;
		}).collect(Collectors.toList());

		List<OrderItem> savedItems = orderItemRepo.saveAll(orderItems);
		log.info("Order items saved for order ID: {}", savedOrder.getId());

		savedOrder.setItems(savedItems);
		return savedOrder;
	}

	@Override
	public List<Order> getAllOrders() {
		List<Order> orders = orderRepo.findAll();
		return orders;
	}

	@Override
	public List<Order> getOrdersByUser(String userEmail) {
		User user = userService.getUserByEmail(userEmail);
		List<Order> orders = orderRepo.findAllByUser(user);
		return orders;
	}

	@Override
	@Transactional
	public Order updateOrderStatus(Long id, String status) {
		log.info("Updating status of order ID: {} to {}", id, status);

		// Validate status
		OrderStatus newStatus;
		try {
			newStatus = OrderStatus.valueOf(status.toUpperCase());
		} catch (IllegalArgumentException e) {
			log.error("Invalid status value: {}", status);
			throw new IllegalArgumentException("Invalid order status: " + status);
		}

		// Fetch order
		Order order = orderRepo.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + id));

		// Update and save
		order.setStatus(newStatus);
		Order updatedOrder = orderRepo.save(order);

		log.info("Order ID: {} status updated to {}", updatedOrder.getId(), updatedOrder.getStatus());
		return updatedOrder;
	}
}
