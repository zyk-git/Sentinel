package com.example.giftledger.controller;

import com.example.giftledger.entity.Activity;
import com.example.giftledger.service.ActivityService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping
    public Activity getActivity() {
        return activityService.getOrInit();
    }

    @PostMapping("/update")
    public Map<String, Object> update(
            @RequestParam String title,
            @RequestParam String date,
            @RequestParam String location,
            @RequestParam(required = false) MultipartFile wxQrcode,
            @RequestParam(required = false) MultipartFile aliQrcode) throws IOException {
        Activity activity = activityService.update(title, date, location, wxQrcode, aliQrcode);
        return Map.of("message", "活动信息更新成功", "data", activity);
    }
}
