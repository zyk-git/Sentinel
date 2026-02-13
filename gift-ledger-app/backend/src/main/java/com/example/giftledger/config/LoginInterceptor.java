package com.example.giftledger.config;

import com.example.giftledger.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    public LoginInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        // 公开接口：提交记录与读取活动信息
        if (("/api/records".equals(uri) && "POST".equalsIgnoreCase(method))
                || "/api/activity".equals(uri)
                || "/api/login".equals(uri)) {
            return true;
        }

        if (authService.isLogin(request.getSession())) {
            return true;
        }
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\":\"请先登录后台\"}");
        return false;
    }
}
