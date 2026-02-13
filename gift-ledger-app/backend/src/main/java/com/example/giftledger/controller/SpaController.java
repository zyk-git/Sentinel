package com.example.giftledger.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    // 前端使用 history 路由时，刷新页面仍返回 index.html
    @GetMapping(value = {"/", "/admin", "/admin/dashboard"})
    public String index() {
        return "forward:/index.html";
    }
}
