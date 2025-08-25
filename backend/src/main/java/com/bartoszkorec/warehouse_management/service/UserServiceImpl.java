package com.bartoszkorec.warehouse_management.service;

import com.bartoszkorec.warehouse_management.entity.User;
import com.bartoszkorec.warehouse_management.exception.UserNotFoundException;
import com.bartoszkorec.warehouse_management.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email: " + email + " not found"));
    }
}
