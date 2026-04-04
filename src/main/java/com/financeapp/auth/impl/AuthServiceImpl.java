package com.financeapp.auth.impl;

import com.financeapp.auth.AuthService;
import com.financeapp.auth.dto.RegisterRequest;
import com.financeapp.common.exception.AppException;
import com.financeapp.config.jwt.JwtService;
import com.financeapp.user.domain.Role;
import com.financeapp.user.domain.User;
import com.financeapp.user.domain.UserStatus;
import com.financeapp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;



@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public void register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        String fullName = request.getFullName().trim();
        String password  = request.getPassword();

        if(userRepository.findByEmail(email).isPresent()){
            throw new AppException("Email already exists", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setStatus(UserStatus.ACTIVE);
        user.setRole(Role.VIEWER);
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

    }

    @Override
    public String login(String email, String password){
        String normalizeEmail = email.trim().toLowerCase();

        User user = userRepository.findByEmail(normalizeEmail)
                .orElseThrow(()-> new AppException("Invaild email or password", HttpStatus.UNAUTHORIZED));

        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new AppException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }

        return jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );
    }
}
