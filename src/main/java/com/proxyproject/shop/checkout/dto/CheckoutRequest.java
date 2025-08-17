package com.proxyproject.shop.checkout.dto;

import com.proxyproject.shop.payments.domain.PaymentMethod;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class CheckoutRequest {

    @NotNull
    private Long customerId;

    @NotEmpty
    private List<CheckoutItemRequest> items;

    @NotNull
    private PaymentMethod paymentMethod;

    // Opcional: si no lo envían, usamos el total de la orden
    private BigDecimal amount;

    // (Opcional) para idempotencia simple, si quieres
    private String clientReferenceId;

    // getters/setters
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public List<CheckoutItemRequest> getItems() { return items; }
    public void setItems(List<CheckoutItemRequest> items) { this.items = items; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getClientReferenceId() { return clientReferenceId; }
    public void setClientReferenceId(String clientReferenceId) { this.clientReferenceId = clientReferenceId; }
}
