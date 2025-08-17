package com.proxyproject.shop.payments.domain;

import java.util.UUID;

public class PaymentResult {
    private final boolean success;
    private final String transactionId;
    private final String message;

    private PaymentResult(boolean success, String transactionId, String message) {
        this.success = success;
        this.transactionId = transactionId;
        this.message = message;
    }

    public static PaymentResult ok(String message) {
        return new PaymentResult(true, UUID.randomUUID().toString(), message);
    }

    public static PaymentResult fail(String message) {
        return new PaymentResult(false, null, message);
    }

    public boolean isSuccess() { return success; }
    public String getTransactionId() { return transactionId; }
    public String getMessage() { return message; }
}
