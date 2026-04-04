package com.financeapp.user.dto;

import com.financeapp.user.domain.Role;
import com.financeapp.user.domain.UserStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UserResponse {
    private UUID id;
    private String email;
    private String fullName;
    private Role role;
    private UserStatus userStatus;
    private LocalDateTime createdAt;
}
