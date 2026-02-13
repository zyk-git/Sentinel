package com.example.giftledger.repository;

import com.example.giftledger.entity.GiftRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface GiftRecordRepository extends JpaRepository<GiftRecord, Long> {
    long countByIpAndSubmitTimeAfter(String ip, LocalDateTime afterTime);
    List<GiftRecord> findByNameContainingOrPayerNameContainingOrderBySubmitTimeDesc(String name, String payerName);
    List<GiftRecord> findAllByOrderBySubmitTimeDesc();
}
