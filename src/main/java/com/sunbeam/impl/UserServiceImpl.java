//package com.sunbeam.impl;
//
//import java.util.Optional;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.sunbeam.exceptions.UserNotFoundException;
//import com.sunbeam.models.User;
//import com.sunbeam.repository.UserRepo;
//import com.sunbeam.service.UserService;
//
//import jakarta.transaction.Transactional;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Service
//public class UserServiceImpl implements UserService {
//
//	@Autowired
//	private UserRepo repo;
//
//	@Override
//	public User getUserById(Long id) {
//		log.info("Fetching user by ID: {}", id);
//		return repo.findByIdAndEnabledTrue(id).orElseThrow(() -> {
//			log.warn("User with ID {} not found", id);
//			return new UserNotFoundException("User with ID: " + id + " does not exist.");
//		});
//	}
//
//	@Override
//	@Transactional
//	public User getUserByEmail(String email) {
//		log.info("Fetching user by Email: {}", email);
//		return repo.findByEmailAndEnabledTrue(email).orElseThrow(() -> {
//			log.warn("User with Email {} not found", email);
//			return new UserNotFoundException("User with Email: " + email + " does not exist.");
//		});
//	}
//
//	@Override
//	public User deleteUserById(Long id) {
//		log.info("Deleting user by ID: {}", id);
//		Optional<User> user = repo.findById(id);
//		if (user.isEmpty()) {
//			log.warn("User with ID {} not found", id);
//			throw new UserNotFoundException("User with ID: " + id + " does not exist.");
//		}
//		repo.deleteById(id);
//		log.info("User with ID {} deleted successfully", id);
//		return user.get();
//	}
//
//	@Override
//	public User updateUserById(Long id) {
//		log.info("Updating user by ID: {}", id);
//		Optional<User> user = repo.findById(id);
//		if (user.isEmpty()) {
//			log.warn("User with ID {} not found", id);
//			throw new UserNotFoundException("User with ID: " + id + " does not exist.");
//		}
//		// Add logic to update user details here
//		log.info("User with ID {} updated successfully", id);
//		return user.get();
//	}
//}


package com.sunbeam.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sunbeam.exceptions.UserNotFoundException;
import com.sunbeam.models.Role;
import com.sunbeam.models.User;
import com.sunbeam.repository.UserRepo;
import com.sunbeam.service.UserService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepo repo;

    public UserServiceImpl(UserRepo repo) {
        this.repo = repo;
    }

    public User getUserById(Long id) {
        log.info("Fetching user by ID: {}", id);
        return repo.findByIdAndEnabledTrue(id).orElseThrow(() -> {
            log.warn("User with ID {} not found or disabled", id);
            return new UserNotFoundException("User with ID: " + id + " does not exist.");
        });
    }

    @Transactional
    public User getUserByEmail(String email) {
        log.info("Fetching user by Email: {}", email);
        return repo.findByEmailAndEnabledTrue(email).orElseThrow(() -> {
            log.warn("User with Email {} not found or disabled", email);
            return new UserNotFoundException("User with Email: " + email + " does not exist.");
        });
    }

    @Override
    public User deleteUserById(Long id) {
        User existing = repo.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User with ID: " + id + " does not exist."));
        repo.delete(existing);
        return existing;
    }


    @Override
    @Transactional
    public User updateUserById(Long id, User updatedUser) {
        log.info("Updating user by ID: {}", id);
        User existing = repo.findById(id).orElseThrow(() -> {
            log.warn("User with ID {} not found", id);
            return new UserNotFoundException("User with ID: " + id + " does not exist.");
        });

        existing.setName(updatedUser.getName());
        existing.setEmail(updatedUser.getEmail());
        existing.setPhone(updatedUser.getPhone());
        // Only update enabled flag if present in payload
        existing.setEnabled(updatedUser.isEnabled());

        User saved = repo.save(existing);
        log.info("User with ID {} updated successfully", id);
        return saved;
    }

    // -------- Customer-specific methods (role = CUSTOMER) --------

    @Override
    public List<User> getAllCustomers() {
        return repo.findByRole(Role.ROLE_CUSTOMER);
    }

    // Keep create for admin tools (even if you don't use it from UI right now)
    public User createCustomer(User customer) {
        customer.setRole(Role.ROLE_CUSTOMER);
        customer.setEnabled(true);
        return repo.save(customer);
    }

	@Override
	public User getUserById(String userEmail) {
		// TODO Auto-generated method stub
		return null;
	}
}
