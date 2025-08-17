package com.proxyproject.shop.payments.dto;

import com.proxyproject.shop.payments.domain.PaymentMethod;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class PaymentRequest {

    @NotNull
    private Long orderId;

    @NotNull
    private PaymentMethod method;

    @NotNull
    private BigDecimal amount;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
