package com.sunbeam.repository;

import java.util.Optional;
import com.sunbeam.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.sunbeam.models.User;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
	Optional<User> findByEmailAndEnabledTrue(String email);

	Optional<User> findByIdAndEnabledTrue(Long id);
	
	List<User> findByRole(Role role);
}
