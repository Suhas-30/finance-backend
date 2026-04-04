package com.financeapp.auth;

import com.financeapp.auth.dto.RegisterRequest;

public interface AuthService {
    void register(RegisterRequest request);

    String login(String email, String password);

}
