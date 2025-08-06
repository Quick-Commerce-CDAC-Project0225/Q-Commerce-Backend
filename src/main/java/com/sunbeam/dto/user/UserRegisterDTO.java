package com.sunbeam.dto.user;

import com.sunbeam.models.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisterDTO {
    private String name;
    private String email;
    private String password;
    private String phone;
    private Role role;
}
