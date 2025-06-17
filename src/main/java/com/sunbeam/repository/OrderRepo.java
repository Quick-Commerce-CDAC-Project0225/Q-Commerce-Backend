package com.sunbeam.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunbeam.models.Order;
import com.sunbeam.models.User;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {
	List<Order> findAllByUser(User user);
}
