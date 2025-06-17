package com.sunbeam.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunbeam.models.User;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
	Optional<User> findByEmailAndEnabledTrue(String email);

	Optional<User> findByIdAndEnabledTrue(Long id);
}
