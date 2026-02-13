package com.example.giftledger.service;

import com.example.giftledger.entity.Activity;
import com.example.giftledger.repository.ActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final FileStorageService fileStorageService;

    public ActivityService(ActivityRepository activityRepository, FileStorageService fileStorageService) {
        this.activityRepository = activityRepository;
        this.fileStorageService = fileStorageService;
    }

    public Activity getOrInit() {
        return activityRepository.findById(1L).orElseGet(() -> {
            Activity activity = new Activity();
            activity.setId(1L);
            activity.setTitle("乔迁之喜");
            activity.setDate("2026-01-01");
            activity.setLocation("某某村·某某大院");
            return activityRepository.save(activity);
        });
    }

    public Activity update(String title, String date, String location, MultipartFile wxQrcode, MultipartFile aliQrcode) throws IOException {
        Activity activity = getOrInit();
        activity.setTitle(title);
        activity.setDate(date);
        activity.setLocation(location);
        String wxPath = fileStorageService.saveImage(wxQrcode);
        if (wxPath != null) {
            activity.setWxQrcodePath(wxPath);
        }
        String aliPath = fileStorageService.saveImage(aliQrcode);
        if (aliPath != null) {
            activity.setAliQrcodePath(aliPath);
        }
        return activityRepository.save(activity);
    }
}
