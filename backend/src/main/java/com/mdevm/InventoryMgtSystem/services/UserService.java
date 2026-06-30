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

    User getCurrentLoggedInUser();

    Response getUserById(Long id);

    Response updateUser(Long id, UserDTO userDTO);

    Response deleteUser(Long id);

    Response getUserTransactions(Long id);
}
