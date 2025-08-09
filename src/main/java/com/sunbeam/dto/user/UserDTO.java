//package com.sunbeam.dto.user;
//
//import java.time.Instant;
//
//import com.sunbeam.models.Role;
//import com.sunbeam.models.User;
//
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class UserDTO {
//	private Long id;
//	private String name;
//	private String email;
//	private String phone;
//	private Role role;
//	private Instant created_at;
//	private Instant updated_at;
//
//	public UserDTO(User user) {
//		this.id = user.getId();
//		this.name = user.getName();
//		this.email = user.getEmail();
//		this.phone = user.getPhone();
//		this.role = user.getRole();
//		this.created_at = user.getCreatedAt();
//		this.updated_at = user.getUpdatedAt();
//	}
//
//}



package com.sunbeam.dto.user;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sunbeam.models.Role;
import com.sunbeam.models.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private Role role;
    private boolean enabled;
    private Instant createdAt;
    private Instant updatedAt;

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.role = user.getRole();
        this.enabled = user.isEnabled();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }

    public static UserDTO from(User user) {
        return new UserDTO(user);
    }
}

