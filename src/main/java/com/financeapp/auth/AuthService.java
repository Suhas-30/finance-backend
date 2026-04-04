package com.financeapp.auth;

import com.financeapp.auth.dto.ChangePasswordRequest;
import com.financeapp.auth.dto.RegisterRequest;
import org.springframework.security.core.Authentication;

public interface AuthService {
    void register(RegisterRequest request);

    String login(String email, String password);
    void changePassword(ChangePasswordRequest request, Authentication authentication);
}
