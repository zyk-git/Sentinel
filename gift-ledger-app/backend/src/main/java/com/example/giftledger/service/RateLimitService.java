package com.example.giftledger.service;

import com.example.giftledger.repository.GiftRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RateLimitService {

    private final GiftRecordRepository giftRecordRepository;

    public RateLimitService(GiftRecordRepository giftRecordRepository) {
        this.giftRecordRepository = giftRecordRepository;
    }

    // 防刷规则：同一IP在1分钟内最多提交3次
    public boolean allowed(String ip) {
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        long count = giftRecordRepository.countByIpAndSubmitTimeAfter(ip, oneMinuteAgo);
        return count < 3;
    }
}
