package com.proxyproject.shop.orders.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class OrderItemRequest {

    @NotNull
    private Long productId;

    @NotNull @Min(1)
    private Integer quantity;

    @NotNull
    private BigDecimal price; // para la prueba técnica, el precio llega del cliente

    // getters/setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
