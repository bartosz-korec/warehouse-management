package com.bartoszkorec.warehouse_management.service;

import com.bartoszkorec.warehouse_management.entity.User;

public interface UserService {

    User findUserByEmail(String email);
}
