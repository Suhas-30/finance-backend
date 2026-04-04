package com.financeapp.user.controller;

import com.financeapp.common.response.ApiResponse;
import com.financeapp.user.dto.*;
import com.financeapp.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @GetMapping("")
    public ApiResponse<List<UserResponse>> getAllUsers(){
        List<UserResponse> users = userService.getAllUsers();
        return new ApiResponse<>(true, "UserFetched successfully", users);
    }

    @PostMapping("")
    public ApiResponse<CreateUserResponse> createUser(@Valid @RequestBody CreateUserRequest request){
        CreateUserResponse response = userService.createUser(request);
        return new ApiResponse<>(true, "User created successfully", response);
    }

    @PatchMapping("/{id}/role")
    public ApiResponse<Void> updateUserRole(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRoleRequest request,
            Authentication authentication
    ){
        userService.updateUserRole(id, request, authentication);
        return  new ApiResponse<>(true, "User role updated Successfully", null);
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<Void> updateUserStatus(@PathVariable UUID id, @Valid @RequestBody UpdateUserStatusRequest request, Authentication authentication){
        userService.updateUserStatus(id, request, authentication);
        return new ApiResponse<>(true, "User status updated successfully", null);
    }
}
