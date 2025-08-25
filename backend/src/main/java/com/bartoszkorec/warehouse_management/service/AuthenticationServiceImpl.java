package com.bartoszkorec.warehouse_management.service;

import com.bartoszkorec.warehouse_management.dto.request.LoginRequest;
import com.bartoszkorec.warehouse_management.dto.response.LoginResponse;
import com.bartoszkorec.warehouse_management.entity.User;
import com.bartoszkorec.warehouse_management.security.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtService jwtService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;


    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );

        User user = userService.findUserByEmail(loginRequest.email());

        String jwtToken = jwtService.generateToken(user);

        return LoginResponse.builder()
                .token(jwtToken)
                .expiresIn(jwtService.getJwtExpiration())
                .build();
    }
}
