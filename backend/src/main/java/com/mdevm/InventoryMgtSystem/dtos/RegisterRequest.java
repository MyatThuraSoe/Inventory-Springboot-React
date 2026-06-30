package com.mdevm.InventoryMgtSystem.dtos;


import com.mdevm.InventoryMgtSystem.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "phoneNumber is required")
    private String phoneNumber;

    // Role is intentionally removed from registration request
    // All new users are assigned MANAGER role by default in UserServiceImpl
    // Admin role can only be assigned by existing admins through user update endpoint

}
