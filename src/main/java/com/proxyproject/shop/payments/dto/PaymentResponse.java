package com.proxyproject.shop.payments.dto;

import java.math.BigDecimal;

public class PaymentResponse {
    private String status;        // PAID / ALREADY_PAID
    private Long orderId;
    private String transactionId; // null si already paid
    private String method;        // CREDIT_CARD / PAYPAL / CRYPTO
    private String message;
    private BigDecimal amount;

    public PaymentResponse(String status, Long orderId, String transactionId, String method, String message, BigDecimal amount) {
        this.status = status;
        this.orderId = orderId;
        this.transactionId = transactionId;
        this.method = method;
        this.message = message;
        this.amount = amount;
    }

    public static PaymentResponse paid(Long orderId, String txId, String method, String message, BigDecimal amount) {
        return new PaymentResponse("PAID", orderId, txId, method, message, amount);
    }

    public static PaymentResponse alreadyPaid(Long orderId) {
        return new PaymentResponse("ALREADY_PAID", orderId, null, null, "Order is already PAID", null);
    }

    public String getStatus() { return status; }
    public Long getOrderId() { return orderId; }
    public String getTransactionId() { return transactionId; }
    public String getMethod() { return method; }
    public String getMessage() { return message; }
    public BigDecimal getAmount() { return amount; }
}
