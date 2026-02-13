package com.example.giftledger.service;

import com.example.giftledger.dto.GiftItemRequest;
import com.example.giftledger.dto.SubmitRecordsRequest;
import com.example.giftledger.entity.GiftRecord;
import com.example.giftledger.repository.GiftRecordRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class GiftRecordService {

    private final GiftRecordRepository giftRecordRepository;

    public GiftRecordService(GiftRecordRepository giftRecordRepository) {
        this.giftRecordRepository = giftRecordRepository;
    }

    public Map<String, Object> submit(SubmitRecordsRequest request, String ip) {
        String submitId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        BigDecimal total = BigDecimal.ZERO;
        List<GiftRecord> entities = new ArrayList<>();

        for (GiftItemRequest item : request.getItems()) {
            GiftRecord record = new GiftRecord();
            record.setSubmitId(submitId);
            record.setName(item.getName());
            record.setAmount(item.getAmount());
            record.setRelation(item.getRelation());
            record.setBlessing(item.getBlessing());
            record.setPayerName(request.getPayerName());
            record.setPayerPhone(request.getPayerPhone());
            record.setSubmitTime(now);
            record.setIp(ip);
            entities.add(record);
            total = total.add(item.getAmount());
        }

        giftRecordRepository.saveAll(entities);

        Map<String, Object> result = new HashMap<>();
        result.put("submitId", submitId);
        result.put("total", total);
        return result;
    }

    public List<GiftRecord> list(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return giftRecordRepository.findAllByOrderBySubmitTimeDesc();
        }
        return giftRecordRepository.findByNameContainingOrPayerNameContainingOrderBySubmitTimeDesc(keyword, keyword);
    }

    public BigDecimal totalAmount(List<GiftRecord> records) {
        return records.stream().map(GiftRecord::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void delete(Long id) {
        giftRecordRepository.deleteById(id);
    }

    public void clearAll() {
        giftRecordRepository.deleteAll();
    }

    public byte[] exportExcel(List<GiftRecord> records) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("礼金记录");
            Row header = sheet.createRow(0);
            String[] columns = {"提交时间", "代付人", "代付人手机号", "姓名", "金额", "关系", "祝福语", "提交批次", "IP"};
            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            int rowIndex = 1;
            for (GiftRecord record : records) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(String.valueOf(record.getSubmitTime()));
                row.createCell(1).setCellValue(record.getPayerName());
                row.createCell(2).setCellValue(Optional.ofNullable(record.getPayerPhone()).orElse(""));
                row.createCell(3).setCellValue(record.getName());
                row.createCell(4).setCellValue(record.getAmount().doubleValue());
                row.createCell(5).setCellValue(record.getRelation());
                row.createCell(6).setCellValue(Optional.ofNullable(record.getBlessing()).orElse(""));
                row.createCell(7).setCellValue(record.getSubmitId());
                row.createCell(8).setCellValue(Optional.ofNullable(record.getIp()).orElse(""));
            }
            workbook.write(output);
            return output.toByteArray();
        }
    }
}
