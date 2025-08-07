package com.sunbeam.service;

import java.util.List;

import com.sunbeam.dto.order.OrderRequestDto;
import com.sunbeam.models.Order;

public interface OrderService {

	Order createOrder(OrderRequestDto orderReq, String userEmail);

	List<Order> getAllOrders();

	List<Order> getOrdersByUser(String userEmail);
	
	Order updateOrderStatus(Long id, String status);


}
