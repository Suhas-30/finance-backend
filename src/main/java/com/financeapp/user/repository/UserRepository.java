package com.financeapp.user.repository;


import com.financeapp.user.domain.Role;
import com.financeapp.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean  existsByRole(Role role);
    boolean existsByEmail(String email);

}