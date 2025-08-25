package com.bartoszkorec.warehouse_management.utils;

import com.bartoszkorec.warehouse_management.entity.User;
import com.bartoszkorec.warehouse_management.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@AllArgsConstructor
public class PopulateDatabaseWithSampleData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String email = "user@test.com";
        if (userRepository.findByEmail(email).isEmpty()) {
            User user = new User();
            user.setFirstName("Test");
            user.setLastName("User");
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode("test"));
            user.setAuthorities(Collections.emptyList());

            userRepository.save(user);
        }
    }
}
