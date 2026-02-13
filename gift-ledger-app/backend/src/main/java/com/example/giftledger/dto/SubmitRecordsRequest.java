package com.example.giftledger.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class SubmitRecordsRequest {

    @NotBlank(message = "代付人姓名不能为空")
    private String payerName;

    private String payerPhone;

    @Valid
    @NotEmpty(message = "至少提交一条记录")
    private List<GiftItemRequest> items;

    public String getPayerName() { return payerName; }
    public void setPayerName(String payerName) { this.payerName = payerName; }
    public String getPayerPhone() { return payerPhone; }
    public void setPayerPhone(String payerPhone) { this.payerPhone = payerPhone; }
    public List<GiftItemRequest> getItems() { return items; }
    public void setItems(List<GiftItemRequest> items) { this.items = items; }
}
