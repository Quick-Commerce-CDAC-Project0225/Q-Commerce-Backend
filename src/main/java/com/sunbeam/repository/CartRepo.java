package com.sunbeam.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunbeam.models.Cart;
import com.sunbeam.models.User;

@Repository
public interface CartRepo extends JpaRepository<Cart, Long> {
	Optional<Cart> findByUser(User user);
}
