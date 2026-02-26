package com.aimarketplace.service;

import com.aimarketplace.dto.LoginRequest;
import com.aimarketplace.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    void logout(String sessionId);
    LoginResponse getCurrentUser(Long userId);
}
