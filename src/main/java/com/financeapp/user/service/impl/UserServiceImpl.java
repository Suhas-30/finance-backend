package com.financeapp.user.service.impl;

import com.financeapp.common.exception.AppException;
import com.financeapp.user.domain.User;
import com.financeapp.user.domain.UserStatus;
import com.financeapp.user.dto.*;
import com.financeapp.user.repository.UserRepository;
import com.financeapp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user->{
            UserResponse res = new UserResponse();
            res.setId(user.getId());
            res.setEmail(user.getEmail());
            res.setFullName(user.getFullName());
            res.setRole(user.getRole());
            res.setUserStatus(user.getStatus());
            res.setCreatedAt(user.getCreatedAt());
            return res;
        }).toList();
    }

    @Override
    public CreateUserResponse createUser(CreateUserRequest request){
        String email = request.getEmail().trim().toLowerCase();

        if(userRepository.existsByEmail(email)){
            throw  new AppException("User already exists with this email", HttpStatus.CONFLICT);
        }

        String rawPassword = UUID.randomUUID().toString().substring(0, 8);

        User user = new User();
        user.setEmail(email);
        user.setFullName(request.getFullName());
        user.setRole(request.getRole());
        user.setStatus(UserStatus.ACTIVE);
        user.setPassword(passwordEncoder.encode(rawPassword));

        userRepository.save(user);

        return new CreateUserResponse(
                rawPassword,
                "User created successfully. Please change password after first login."
        );
    }

    @Override
    public void updateUserRole(UUID userId, UpdateUserRoleRequest request, Authentication authentication) {
        String currentEmail = authentication.getName();

        User currentuser = userRepository.findByEmail(currentEmail).orElseThrow(()->new AppException("User not found", HttpStatus.NOT_FOUND));

        User targetuser = userRepository.findById(userId).orElseThrow(()-> new AppException("User not found", HttpStatus.NOT_FOUND));

        if(currentuser.getId().equals(targetuser.getId())){
            throw new AppException("You cannot change your own role", HttpStatus.BAD_REQUEST);
        }
        targetuser.setRole(request.getRole());

        userRepository.save(targetuser);
    }

    @Override
    public void updateUserStatus(UUID userId, UpdateUserStatusRequest request, Authentication authentication) {
        String currentEmail = authentication.getName();

        User currentuser = userRepository.findByEmail(currentEmail).orElseThrow(()->new AppException("User not found", HttpStatus.NOT_FOUND));

        User targetuser = userRepository.findById(userId).orElseThrow(()-> new AppException("User not found", HttpStatus.NOT_FOUND));

        if(currentuser.getId().equals(targetuser.getId())){
            throw new AppException("You cannot change your own status", HttpStatus.BAD_REQUEST);
        }

        targetuser.setStatus(request.getStatus());

        userRepository.save(targetuser);

    }
}
