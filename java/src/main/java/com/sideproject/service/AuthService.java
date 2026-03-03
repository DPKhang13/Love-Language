package com.sideproject.service;

import com.sideproject.dto.LoginRequest;
import com.sideproject.dto.LoginResponse;
import com.sideproject.dto.RegisterRequest;
import com.sideproject.dto.UserDto;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
    UserDto register(RegisterRequest registerRequest);
    LoginResponse refreshToken(String refreshToken);
}

