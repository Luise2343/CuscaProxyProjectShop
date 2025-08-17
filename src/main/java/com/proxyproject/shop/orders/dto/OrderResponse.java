package com.proxyproject.shop.orders.dto;

import com.proxyproject.shop.orders.domain.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class OrderResponse {
    private Long id;
    private Long customerId;
    private OrderStatus status;
    private Instant createdAt;
    private BigDecimal total;
    private List<OrderItemResponse> items;

    public OrderResponse(Long id, Long customerId, OrderStatus status,
                         Instant createdAt, BigDecimal total,
                         List<OrderItemResponse> items) {
        this.id = id;
        this.customerId = customerId;
        this.status = status;
        this.createdAt = createdAt;
        this.total = total;
        this.items = items;
    }

    public Long getId() { return id; }
    public Long getCustomerId() { return customerId; }
    public OrderStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public BigDecimal getTotal() { return total; }
    public List<OrderItemResponse> getItems() { return items; }
}
