package com.proxyproject.shop.checkout.dto;

import com.proxyproject.shop.orders.dto.OrderItemResponse;
import com.proxyproject.shop.orders.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class CheckoutResponse {
    private Long orderId;
    private Long customerId;
    private OrderStatus status;
    private Instant createdAt;
    private BigDecimal total;
    private List<OrderItemResponse> items;

    // Datos del pago
    private String paymentStatus;     // PAID / ALREADY_PAID
    private String transactionId;
    private String paymentMethod;
    private String paymentMessage;
    private BigDecimal amount;

    public CheckoutResponse(Long orderId, Long customerId, OrderStatus status, Instant createdAt, BigDecimal total,
                            List<OrderItemResponse> items,
                            String paymentStatus, String transactionId, String paymentMethod, String paymentMessage, BigDecimal amount) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.status = status;
        this.createdAt = createdAt;
        this.total = total;
        this.items = items;
        this.paymentStatus = paymentStatus;
        this.transactionId = transactionId;
        this.paymentMethod = paymentMethod;
        this.paymentMessage = paymentMessage;
        this.amount = amount;
    }

    // getters (si prefieres, agrega setters)
    public Long getOrderId() { return orderId; }
    public Long getCustomerId() { return customerId; }
    public OrderStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public BigDecimal getTotal() { return total; }
    public List<OrderItemResponse> getItems() { return items; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getTransactionId() { return transactionId; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentMessage() { return paymentMessage; }
    public BigDecimal getAmount() { return amount; }
}
