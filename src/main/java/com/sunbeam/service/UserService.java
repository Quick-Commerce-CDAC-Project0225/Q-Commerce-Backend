//package com.sunbeam.service;
//
//import org.springframework.stereotype.Service;
//
//import com.sunbeam.models.User;
//
//@Service
//public interface UserService {
//	public User getUserById(Long id);
//
//	public User getUserByEmail(String email);
//
//	public User deleteUserById(Long id);
//
//	public User updateUserById(Long id);
//}

//package com.sunbeam.service;
//
//import com.sunbeam.models.User;
//import java.util.List;
//
//public interface UserService {
//    User getUserById(Long id);
//    User getUserByEmail(String email);
//    User deleteUserById(Long id);
//    User updateUserById(Long id, User updatedUser);
//
//    // Customer-specific methods
//    List<User> getAllCustomers();
//    User createCustomer(User customer);
//}


package com.sunbeam.service;

import com.sunbeam.models.User;
import java.util.List;

public interface UserService {
    User getUserById(Long id);
    List<User> getAllCustomers();
    User updateUserById(Long id, User updatedUser);
    User deleteUserById(Long id);
	User createCustomer(User customer);
	User getUserByEmail(String userEmail);
	User getUserById(String userEmail);
}

