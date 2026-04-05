package com.financeapp.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financeapp.auth.AuthService;
import com.financeapp.auth.dto.ChangePasswordRequest;
import com.financeapp.auth.dto.LoginRequest;
import com.financeapp.auth.dto.RegisterRequest;
import com.financeapp.config.jwt.JwtAuthFilter;
import com.financeapp.config.jwt.JwtService;
import com.financeapp.config.ratelimit.filter.RateLimitFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = AuthController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        },
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                JwtAuthFilter.class,
                                RateLimitFilter.class
                        }
                )
        }
)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@gmail.com");
        request.setFullName("Test User");
        request.setPassword("123456");

        doNothing().when(authService).register(any(RegisterRequest.class));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("123456");

        when(authService.login("test@gmail.com", "123456"))
                .thenReturn("jwt_token");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Login Successful"))
                .andExpect(jsonPath("$.data.token").value("jwt_token"));
    }

    @Test
    void shouldReturnBadRequestWhenLoginRequestInvalid() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("");
        request.setPassword("");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldChangePasswordSuccessfully() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("123456");
        request.setNewPassword("new123");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@gmail.com");

        doNothing().when(authService)
                .changePassword(any(ChangePasswordRequest.class), any(Authentication.class));

        mockMvc.perform(post("/api/v1/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Password Changed Successfully"));
    }

    @Test
    void shouldReturnBadRequestWhenChangePasswordInvalid() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("");
        request.setNewPassword("");

        mockMvc.perform(post("/api/v1/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(() -> "test@gmail.com"))
                .andExpect(status().isBadRequest());
    }
}