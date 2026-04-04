package com.financeapp.user.service;

import com.financeapp.user.dto.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<UserResponse> getAllUsers();
    CreateUserResponse createUser(CreateUserRequest request);
    void updateUserRole(UUID userId, UpdateUserRoleRequest request, Authentication authentication);
    void updateUserStatus(UUID userId, UpdateUserStatusRequest request, Authentication authentication);
}
