package com.mdevm.InventoryMgtSystem.services;

import com.mdevm.InventoryMgtSystem.dtos.LoginRequest;
import com.mdevm.InventoryMgtSystem.dtos.RegisterRequest;
import com.mdevm.InventoryMgtSystem.dtos.Response;
import com.mdevm.InventoryMgtSystem.dtos.UserDTO;
import com.mdevm.InventoryMgtSystem.models.User;

public interface UserService {
    Response registerUser(RegisterRequest registerRequest);

    Response loginUser(LoginRequest loginRequest);

    Response getAllUsers();

    UserDTO getCurrentLoggedInUser();

    User getCurrentLoggedInUserEntity();

    Response getUserById(Long id);

    Response updateUser(Long id, UserDTO userDTO);

    Response deleteUser(Long id);

    Response getUserTransactions(Long id);

    /**
     * Check if the current user is either an ADMIN or the owner of the given user ID.
     * Used for @PreAuthorize expressions.
     */
    boolean isCurrentUserOrAdmin(Long userId);
}
