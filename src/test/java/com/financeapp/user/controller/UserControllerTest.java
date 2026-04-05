package com.financeapp.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financeapp.user.dto.*;
import com.financeapp.user.service.UserService;
import com.financeapp.user.domain.Role;
import com.financeapp.user.domain.UserStatus;
import com.financeapp.config.jwt.JwtAuthFilter;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = UserController.class,
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
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void shouldGetAllUsers() throws Exception {

        UserResponse user = new UserResponse();
        user.setEmail("test@mail.com");
        user.setFullName("Test User");
        user.setRole(Role.VIEWER);
        user.setUserStatus(UserStatus.ACTIVE);

        when(userService.getAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true)) // ✅ FIXED
                .andExpect(jsonPath("$.message").value("Users fetched successfully"))
                .andExpect(jsonPath("$.data[0].email").value("test@mail.com"));
    }


    @Test
    void shouldCreateUserSuccessfully() throws Exception {

        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("test@mail.com");
        request.setFullName("New User");
        request.setRole(Role.ADMIN);

        CreateUserResponse response =
                new CreateUserResponse("temp1234", "User created successfully");

        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true)) // ✅ FIXED
                .andExpect(jsonPath("$.data.tempPassword").value("temp1234")); // ✅ FIXED
    }


    @Test
    void shouldUpdateUserRoleSuccessfully() throws Exception {

        UUID id = UUID.randomUUID();

        UpdateUserRoleRequest request = new UpdateUserRoleRequest();
        request.setRole(Role.ADMIN);

        Authentication auth =
                new UsernamePasswordAuthenticationToken("admin@mail.com", null);

        doNothing().when(userService)
                .updateUserRole(eq(id), any(UpdateUserRoleRequest.class), any(Authentication.class));

        mockMvc.perform(patch("/api/v1/users/{id}/role", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(auth)) // ✅ FIXED
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true)) // ✅ FIXED
                .andExpect(jsonPath("$.message").value("User role updated successfully"));
    }


    @Test
    void shouldUpdateUserStatusSuccessfully() throws Exception {

        UUID id = UUID.randomUUID();

        UpdateUserStatusRequest request = new UpdateUserStatusRequest();
        request.setStatus(UserStatus.LOCKED);

        Authentication auth =
                new UsernamePasswordAuthenticationToken("admin@mail.com", null);

        doNothing().when(userService)
                .updateUserStatus(eq(id), any(UpdateUserStatusRequest.class), any(Authentication.class));

        mockMvc.perform(patch("/api/v1/users/{id}/status", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(auth)) // ✅ FIXED
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true)) // ✅ FIXED
                .andExpect(jsonPath("$.message").value("User status updated successfully"));
    }


    @Test
    void shouldReturnBadRequestWhenCreateUserInvalid() throws Exception {

        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("");

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}