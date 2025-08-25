package com.bartoszkorec.warehouse_management.service;

import com.bartoszkorec.warehouse_management.dto.request.LoginRequest;
import com.bartoszkorec.warehouse_management.dto.response.LoginResponse;
import com.bartoszkorec.warehouse_management.dto.response.UserResponse;

public interface AuthenticationService {

    LoginResponse login(LoginRequest loginRequest);
}
