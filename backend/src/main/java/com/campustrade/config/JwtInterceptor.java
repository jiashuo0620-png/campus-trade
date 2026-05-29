package com.campustrade.config;

import com.campustrade.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String method = request.getMethod();
        String uri = request.getRequestURI();

        // 放行OPTIONS预检请求
        if ("OPTIONS".equalsIgnoreCase(method)) return true;

        // 放行公开接口
        if (isPublic(method, uri)) return true;

        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            response.setStatus(401);
            return false;
        }
        String token = auth.replace("Bearer ", "");
        if (!jwtUtil.validate(token)) {
            response.setStatus(401);
            return false;
        }
        request.setAttribute("userId", jwtUtil.getUserId(token));
        request.setAttribute("username", jwtUtil.getUsername(token));
        request.setAttribute("role", jwtUtil.getRole(token));
        return true;
    }

    private boolean isPublic(String method, String uri) {
        if (uri.startsWith("/api/user/login") || uri.startsWith("/api/user/register")) return true;
        if (uri.startsWith("/api/admin/login")) return true;
        if ("GET".equals(method) && uri.matches("/api/goods(/\\d+)?") && !uri.contains("/my")) return true;
        if ("GET".equals(method) && uri.startsWith("/api/goods/search")) return true;
        return false;
    }
}
