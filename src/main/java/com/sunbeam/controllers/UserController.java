//package com.sunbeam.controllers;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.sunbeam.dto.ResponseDTO;
//import com.sunbeam.dto.user.UserDTO;
//import com.sunbeam.models.User;
//import com.sunbeam.service.UserService;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@RestController
//@RequestMapping("api/v1/users")
//public class UserController {
//
//	@Autowired
//	private UserService service;
//
//	@GetMapping("/{id}")
//	protected ResponseEntity<ResponseDTO<UserDTO>> getUserById(@RequestParam Long id) {
//		log.info("Fetching user with ID: {}", id);
//		User user = service.getUserById(id);
//		ResponseDTO<UserDTO> response = new ResponseDTO<>(true, "success", new UserDTO(user));
//		log.info("User fetched successfully: {}", user.getId());
//		return new ResponseEntity<>(response, HttpStatus.OK);
//	}
//
//}

package com.sunbeam.controllers;

import com.sunbeam.dto.ResponseDTO;
import com.sunbeam.dto.user.UserDTO;
import com.sunbeam.models.User;
import com.sunbeam.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // ---- Existing: get a single user by ID (wrapped in ResponseDTO) ----
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<UserDTO>> getUserById(@PathVariable Long id) {
        log.info("Fetching user with ID: {}", id);
        User user = service.getUserById(id);
        log.info("User fetched successfully: {}", user.getId());
        return ResponseEntity.ok(new ResponseDTO<>(true, "success", new UserDTO(user)));
    }

    // ---- Manage Customers (role = CUSTOMER) ----

    // GET: list all customers (returns plain array of DTOs)
    @GetMapping("/customers")
    public ResponseEntity<List<UserDTO>> getAllCustomers() {
        var list = service.getAllCustomers().stream().map(UserDTO::new).toList();
        return ResponseEntity.ok(list);
    }

    // PUT: update a customer by id (expects { name, email, phone, enabled } in body)
    @PutMapping("/customers/{id}")
    public ResponseEntity<UserDTO> updateCustomer(@PathVariable Long id, @RequestBody User payload) {
        var updated = service.updateUserById(id, payload);
        return ResponseEntity.ok(new UserDTO(updated));
    }

    // DELETE: delete a customer by id (204 No Content)
    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        service.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}

