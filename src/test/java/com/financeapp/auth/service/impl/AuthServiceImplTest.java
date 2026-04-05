package com.financeapp.auth.service.impl;

import com.financeapp.auth.dto.ChangePasswordRequest;
import com.financeapp.auth.dto.RegisterRequest;
import com.financeapp.auth.impl.AuthServiceImpl;
import com.financeapp.common.exception.AppException;
import com.financeapp.config.jwt.JwtService;
import com.financeapp.user.domain.Role;
import com.financeapp.user.domain.User;
import com.financeapp.user.domain.UserStatus;
import com.financeapp.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    // Test case: Register user successfully when email does not exist
    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@gmail.com");
        request.setFullName("Test User");
        request.setPassword("123456");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("123456"))
                .thenReturn("encoded_password");

        authService.register(request);

        verify(userRepository, times(1)).save(any(User.class));
    }

    // Test case: Throw exception when email already exists during registration
    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@gmail.com");
        request.setFullName("Test User");
        request.setPassword("123456");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(new User()));

        Assertions.assertThrows(AppException.class, () -> {
            authService.register(request);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    // Test case: Login successfully with valid credentials
    @Test
    void shouldLoginSuccessfully() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("encoded_password");
        user.setRole(Role.VIEWER);
        user.setStatus(UserStatus.ACTIVE);

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("123456", "encoded_password"))
                .thenReturn(true);

        when(jwtService.generateToken("test@gmail.com", "VIEWER"))
                .thenReturn("jwt_token");

        String token = authService.login("test@gmail.com", "123456");

        Assertions.assertEquals("jwt_token", token);
    }

    // Test case: Throw exception when email not found during login
    @Test
    void shouldThrowExceptionWhenEmailNotFound() {
        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(AppException.class, () -> {
            authService.login("test@gmail.com", "123456");
        });
    }

    // Test case: Throw exception when password is incorrect during login
    @Test
    void shouldThrowExceptionWhenPasswordInvalid() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("encoded_password");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrong_password", "encoded_password"))
                .thenReturn(false);

        Assertions.assertThrows(AppException.class, () -> {
            authService.login("test@gmail.com", "wrong_password");
        });
    }

    // Test case: Change password successfully when old password is correct
    @Test
    void shouldChangePasswordSuccessfully() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("123456");
        request.setNewPassword("new123");

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("encoded_old");

        when(authentication.getName()).thenReturn("test@gmail.com");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("123456", "encoded_old"))
                .thenReturn(true);

        when(passwordEncoder.encode("new123"))
                .thenReturn("encoded_new");

        authService.changePassword(request, authentication);

        verify(userRepository, times(1)).save(user);
    }

    // Test case: Throw exception when user not found during change password
    @Test
    void shouldThrowExceptionWhenUserNotFoundInChangePassword() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("123456");
        request.setNewPassword("new123");

        when(authentication.getName()).thenReturn("test@gmail.com");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(AppException.class, () -> {
            authService.changePassword(request, authentication);
        });
    }

    // Test case: Throw exception when old password is incorrect
    @Test
    void shouldThrowExceptionWhenOldPasswordIncorrect() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("wrong");
        request.setNewPassword("new123");

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("encoded_old");

        when(authentication.getName()).thenReturn("test@gmail.com");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrong", "encoded_old"))
                .thenReturn(false);

        Assertions.assertThrows(AppException.class, () -> {
            authService.changePassword(request, authentication);
        });
    }

    // Test case: Throw exception when new password is same as old password
    @Test
    void shouldThrowExceptionWhenNewPasswordSameAsOld() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("123456");
        request.setNewPassword("123456");

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("encoded_old");

        when(authentication.getName()).thenReturn("test@gmail.com");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("123456", "encoded_old"))
                .thenReturn(true);

        Assertions.assertThrows(AppException.class, () -> {
            authService.changePassword(request, authentication);
        });
    }
}