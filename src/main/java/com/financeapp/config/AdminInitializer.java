package com.financeapp.config;

import com.financeapp.user.domain.Role;
import com.financeapp.user.domain.User;
import com.financeapp.user.domain.UserStatus;
import com.financeapp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        boolean adminExists = userRepository.existsByRole(Role.ADMIN);

        if(!adminExists){
            User admin = new User();
            admin.setEmail("admin@finance.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("Super Admin");
            admin.setRole(Role.ADMIN);
            admin.setStatus(UserStatus.ACTIVE);

            userRepository.save(admin);

            System.out.println("Default Admin Created");
        }
    }
}
