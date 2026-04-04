package com.financeapp.auth.controller;

import com.financeapp.auth.AuthService;
import com.financeapp.auth.dto.ChangePasswordRequest;
import com.financeapp.auth.dto.LoginRequest;
import com.financeapp.auth.dto.LoginResponse;
import com.financeapp.auth.dto.RegisterRequest;
import com.financeapp.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody RegisterRequest request){
        authService.register(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "User registered successfully", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest){
        String token  = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return  ResponseEntity.ok(new ApiResponse<LoginResponse>(true, "Login Successful", new LoginResponse(token)));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request, Authentication authentication){
        authService.changePassword(request, authentication);
        return  ResponseEntity.ok(new ApiResponse<>(true, "Password Changed Successfully", null));
    }

}
