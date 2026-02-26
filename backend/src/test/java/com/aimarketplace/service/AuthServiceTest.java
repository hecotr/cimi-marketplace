package com.aimarketplace.service;

import com.aimarketplace.dto.LoginRequest;
import com.aimarketplace.dto.LoginResponse;
import com.aimarketplace.entity.User;
import com.aimarketplace.mapper.UserMapper;
import com.aimarketplace.service.impl.AuthServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    private BCryptPasswordEncoder passwordEncoder;
    private User testUser;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        ReflectionTestUtils.setField(authService, "passwordEncoder", passwordEncoder);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setEmail("test@example.com");
        testUser.setRole("user");
        testUser.setDepartment("技术部");
        testUser.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("登录成功 - 正确的用户名和密码")
    void loginSuccess() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);

        LoginResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        assertEquals("user", response.getRole());
    }

    @Test
    @DisplayName("登录失败 - 用户不存在")
    void loginFailUserNotFound() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("nonexistent");
        loginRequest.setPassword("password123");

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
    }

    @Test
    @DisplayName("登录失败 - 密码错误")
    void loginFailWrongPassword() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("wrongpassword");

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);

        assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
    }

    @Test
    @DisplayName("获取当前用户 - 成功")
    void getCurrentUserSuccess() {
        when(userMapper.selectById(1L)).thenReturn(testUser);

        LoginResponse response = authService.getCurrentUser(1L);

        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
    }

    @Test
    @DisplayName("获取当前用户 - 不存在")
    void getCurrentUserNotFound() {
        when(userMapper.selectById(999L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> authService.getCurrentUser(999L));
    }
}
