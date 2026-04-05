package com.financeapp.user.service.impl;

import com.financeapp.common.exception.AppException;
import com.financeapp.user.domain.*;
import com.financeapp.user.dto.*;
import com.financeapp.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserServiceImpl userService;


    @Test
    void shouldReturnAllUsers() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@mail.com");
        user.setFullName("Test User");
        user.setRole(Role.VIEWER);
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponse> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("test@mail.com", result.get(0).getEmail());
        assertEquals(Role.VIEWER, result.get(0).getRole());

        verify(userRepository).findAll();
    }

    @Test
    void shouldCreateUserSuccessfully() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("Test@Mail.com");
        request.setFullName("New User");
        request.setRole(Role.ADMIN);

        when(userRepository.existsByEmail("test@mail.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");

        CreateUserResponse response = userService.createUser(request);

        assertNotNull(response);
        assertNotNull(response.getTempPassword());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();

        assertEquals("test@mail.com", savedUser.getEmail());
        assertEquals("encoded-password", savedUser.getPassword());
        assertEquals(Role.ADMIN, savedUser.getRole());
        assertEquals(UserStatus.ACTIVE, savedUser.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenEmailExists() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("test@mail.com");

        when(userRepository.existsByEmail("test@mail.com")).thenReturn(true);

        AppException ex = assertThrows(AppException.class,
                () -> userService.createUser(request));

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }


    @Test
    void shouldUpdateUserRoleSuccessfully() {
        UUID targetId = UUID.randomUUID();

        User currentUser = new User();
        currentUser.setId(UUID.randomUUID());
        currentUser.setEmail("admin@mail.com");

        User targetUser = new User();
        targetUser.setId(targetId);

        UpdateUserRoleRequest request = new UpdateUserRoleRequest();
        request.setRole(Role.ADMIN);

        when(authentication.getName()).thenReturn("admin@mail.com");
        when(userRepository.findByEmail("admin@mail.com")).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(targetId)).thenReturn(Optional.of(targetUser));

        userService.updateUserRole(targetId, request, authentication);

        assertEquals(Role.ADMIN, targetUser.getRole());
        verify(userRepository).save(targetUser);
    }

    @Test
    void shouldThrowWhenUpdatingOwnRole() {
        UUID id = UUID.randomUUID();

        User currentUser = new User();
        currentUser.setId(id);
        currentUser.setEmail("admin@mail.com");

        UpdateUserRoleRequest request = new UpdateUserRoleRequest();

        when(authentication.getName()).thenReturn("admin@mail.com");
        when(userRepository.findByEmail("admin@mail.com")).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(id)).thenReturn(Optional.of(currentUser));

        AppException ex = assertThrows(AppException.class,
                () -> userService.updateUserRole(id, request, authentication));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }


    @Test
    void shouldUpdateUserStatusSuccessfully() {
        UUID targetId = UUID.randomUUID();

        User currentUser = new User();
        currentUser.setId(UUID.randomUUID());
        currentUser.setEmail("admin@mail.com");

        User targetUser = new User();
        targetUser.setId(targetId);

        UpdateUserStatusRequest request = new UpdateUserStatusRequest();
        request.setStatus(UserStatus.LOCKED);

        when(authentication.getName()).thenReturn("admin@mail.com");
        when(userRepository.findByEmail("admin@mail.com")).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(targetId)).thenReturn(Optional.of(targetUser));

        userService.updateUserStatus(targetId, request, authentication);

        assertEquals(UserStatus.LOCKED, targetUser.getStatus());
        verify(userRepository).save(targetUser);
    }

    @Test
    void shouldThrowWhenUpdatingOwnStatus() {
        UUID id = UUID.randomUUID();

        User currentUser = new User();
        currentUser.setId(id);
        currentUser.setEmail("admin@mail.com");

        UpdateUserStatusRequest request = new UpdateUserStatusRequest();

        when(authentication.getName()).thenReturn("admin@mail.com");
        when(userRepository.findByEmail("admin@mail.com")).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(id)).thenReturn(Optional.of(currentUser));

        AppException ex = assertThrows(AppException.class,
                () -> userService.updateUserStatus(id, request, authentication));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }
}