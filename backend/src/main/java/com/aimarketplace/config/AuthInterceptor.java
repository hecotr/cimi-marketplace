package com.aimarketplace.config;

import com.aimarketplace.entity.User;
import com.aimarketplace.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;

    // 简单的 Session 存储（生产环境应使用 Redis）
    private static final ConcurrentHashMap<String, Long> sessionStore = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, String> userSessionStore = new ConcurrentHashMap<>();

    public static void addSession(String sessionId, Long userId) {
        sessionStore.put(sessionId, userId);
        userSessionStore.put(userId, sessionId);
    }

    public static void removeSession(String sessionId) {
        Long userId = sessionStore.remove(sessionId);
        if (userId != null) {
            userSessionStore.remove(userId);
        }
    }

    public static Long getUserId(String sessionId) {
        return sessionStore.get(sessionId);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 请求
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 放行登录接口
        String uri = request.getRequestURI();
        if (uri.equals("/api/auth/login")) {
            return true;
        }

        // 从 header 或 cookie 获取 sessionId
        String sessionId = request.getHeader("X-Session-Id");
        if (sessionId == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("sessionId".equals(cookie.getName())) {
                        sessionId = cookie.getValue();
                        break;
                    }
                }
            }
        }

        if (sessionId == null || !sessionStore.containsKey(sessionId)) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录\"}");
            return false;
        }

        Long userId = sessionStore.get(sessionId);
        request.setAttribute("userId", userId);

        // 检查管理员权限
        if (uri.startsWith("/api/admin")) {
            User user = userMapper.selectById(userId);
            if (user == null || !"admin".equals(user.getRole())) {
                response.setStatus(403);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":403,\"message\":\"无权限\"}");
                return false;
            }
        }

        return true;
    }
}
