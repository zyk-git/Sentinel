package com.example.giftledger.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public static final String SESSION_LOGIN_KEY = "ADMIN_LOGIN";

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:admin}")
    private String adminPassword;

    public boolean login(String username, String password, HttpSession session) {
        if (adminUsername.equals(username) && adminPassword.equals(password)) {
            session.setAttribute(SESSION_LOGIN_KEY, true);
            return true;
        }
        return false;
    }

    public boolean isLogin(HttpSession session) {
        Object value = session.getAttribute(SESSION_LOGIN_KEY);
        return value instanceof Boolean && (Boolean) value;
    }
}
