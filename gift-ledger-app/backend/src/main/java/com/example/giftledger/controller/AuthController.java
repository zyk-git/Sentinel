package com.example.giftledger.controller;

import com.example.giftledger.dto.LoginRequest;
import com.example.giftledger.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        boolean success = authService.login(request.getUsername(), request.getPassword(), session);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "登录成功" : "用户名或密码错误");
        return result;
    }
}
