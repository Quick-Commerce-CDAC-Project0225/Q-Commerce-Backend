package com.sunbeam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunbeam.models.CartItem;

@Repository
public interface CartItemRepo extends JpaRepository<CartItem, Long> {

}
