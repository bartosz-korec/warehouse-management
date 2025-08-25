package com.bartoszkorec.warehouse_management.controller;

import com.bartoszkorec.warehouse_management.dto.request.LoginRequest;
import com.bartoszkorec.warehouse_management.dto.response.LoginResponse;
import com.bartoszkorec.warehouse_management.service.AuthenticationService;
import com.bartoszkorec.warehouse_management.security.JwtService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@AllArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        return authenticationService.login(loginRequest);
    }
}
