package com.proxyproject.shop.payments.dto;

import java.math.BigDecimal;

public class PaymentResponse {
    private String status;
    private String transactionId;
    private String method;
    private String message;
    private BigDecimal amount;

    public PaymentResponse() {
    }

    public PaymentResponse(String status, String transactionId, String method, String message, BigDecimal amount) {
        this.status = status;
        this.transactionId = transactionId;
        this.method = method;
        this.message = message;
        this.amount = amount;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
