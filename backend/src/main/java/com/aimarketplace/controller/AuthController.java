package com.aimarketplace.controller;

import com.aimarketplace.common.Result;
import com.aimarketplace.config.AuthInterceptor;
import com.aimarketplace.dto.LoginRequest;
import com.aimarketplace.dto.LoginResponse;
import com.aimarketplace.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request,
                                       HttpServletResponse response) {
        try {
            LoginResponse loginResponse = authService.login(request);
            // 生成并存储 sessionId
            String sessionId = UUID.randomUUID().toString();
            AuthInterceptor.addSession(sessionId, loginResponse.getId());
            // 设置到 header 和 cookie
            response.setHeader("X-Session-Id", sessionId);
            javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie("sessionId", sessionId);
            cookie.setPath("/");
            cookie.setMaxAge(30 * 24 * 60 * 60); // 30 天
            cookie.setHttpOnly(true);
            // cookie.setSecure(true); // 生产环境启用 HTTPS 时取消注释
            response.addCookie(cookie);
            return Result.success(loginResponse);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String sessionId = request.getHeader("X-Session-Id");
        if (sessionId != null) {
            authService.logout(sessionId);
        }
        return Result.success();
    }

    @GetMapping("/me")
    public Result<LoginResponse> getCurrentUser(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error(401, "未登录");
            }
            LoginResponse user = authService.getCurrentUser(userId);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
