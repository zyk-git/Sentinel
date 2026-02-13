package com.example.giftledger.controller;

import com.example.giftledger.dto.SubmitRecordsRequest;
import com.example.giftledger.entity.GiftRecord;
import com.example.giftledger.service.GiftRecordService;
import com.example.giftledger.service.RateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/records")
public class RecordController {

    private final GiftRecordService giftRecordService;
    private final RateLimitService rateLimitService;

    public RecordController(GiftRecordService giftRecordService, RateLimitService rateLimitService) {
        this.giftRecordService = giftRecordService;
        this.rateLimitService = rateLimitService;
    }

    @PostMapping
    public Map<String, Object> submit(@Valid @RequestBody SubmitRecordsRequest request, HttpServletRequest servletRequest) {
        if (request.getItems().size() > 10) {
            return Map.of("message", "最多提交10条记录");
        }
        String ip = getClientIp(servletRequest);
        if (!rateLimitService.allowed(ip)) {
            return Map.of("message", "提交太频繁，请稍后再试");
        }
        Map<String, Object> result = giftRecordService.submit(request, ip);
        result.put("message", "记录成功");
        return result;
    }

    @GetMapping
    public Map<String, Object> list(@RequestParam(required = false) String keyword) {
        List<GiftRecord> records = giftRecordService.list(keyword);
        Map<String, Object> result = new HashMap<>();
        result.put("list", records);
        result.put("totalAmount", giftRecordService.totalAmount(records));
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        giftRecordService.delete(id);
        return Map.of("message", "删除成功");
    }

    @DeleteMapping("/clear")
    public Map<String, Object> clear() {
        giftRecordService.clearAll();
        return Map.of("message", "全部清空成功");
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@RequestParam(required = false) String keyword) throws IOException {
        List<GiftRecord> records = giftRecordService.list(keyword);
        byte[] fileData = giftRecordService.exportExcel(records);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=gift-records.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileData);
    }

    private String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
