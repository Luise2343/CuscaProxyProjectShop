package com.proxyproject.shop.checkout.app;

import com.proxyproject.shop.checkout.dto.CheckoutItemRequest;
import com.proxyproject.shop.checkout.dto.CheckoutRequest;
import com.proxyproject.shop.checkout.dto.CheckoutResponse;

import com.proxyproject.shop.orders.dto.CreateOrderRequest;
import com.proxyproject.shop.orders.dto.OrderItemRequest;
import com.proxyproject.shop.orders.dto.OrderResponse;
import com.proxyproject.shop.orders.service.OrderService;

import com.proxyproject.shop.payments.app.PaymentService;
import com.proxyproject.shop.payments.dto.PaymentResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CheckoutFacade {

    private final OrderService orderService;
    private final PaymentService paymentService;

    public CheckoutFacade(OrderService orderService, PaymentService paymentService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    /**
     * Flow:
     * 1) Build CreateOrderRequest from checkout items (no price; OrderService re-prices).
     * 2) Create order -> OrderService re-prices items and sets total.
     * 3) If request.amount provided, it must equal order.total; otherwise 400.
     * 4) Pay. PaymentService should mark order as PAID if successful.
     * 5) Refresh order and return final status + payment info.
     */
    @Transactional
    public CheckoutResponse checkout(CheckoutRequest request) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Items are required");
        }

        // 1) Create order request (do NOT trust client price)
        CreateOrderRequest createOrder = new CreateOrderRequest();
        createOrder.setCustomerId(request.getCustomerId());
        createOrder.setItems(mapItems(request.getItems()));

        // 2) Create order (OrderService already re-prices and computes total)
        OrderResponse order = orderService.create(createOrder);

        // 3) Amount to charge: if provided, must match recalculated total
        BigDecimal amount = request.getAmount() != null ? request.getAmount() : order.getTotal();
        if (amount == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Amount is required or must be derivable from order total");
        }
        if (request.getAmount() != null && order.getTotal() != null
                && amount.compareTo(order.getTotal()) != 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment amount must equal order total (expected " + order.getTotal() + ")"
            );
        }

        // 4) Pay (service validates states and amount vs total)
        PaymentResponse payment = paymentService.pay(order.getId(), request.getPaymentMethod(), amount);

        // 5) Refresh order to return final status (e.g., PAID)
        OrderResponse refreshed = orderService.getById(order.getId());

        // 6) Build aggregated response
        return new CheckoutResponse(
                refreshed.getId(),
                refreshed.getCustomerId(),
                refreshed.getStatus(),
                refreshed.getCreatedAt(),
                refreshed.getTotal(),
                refreshed.getItems(),
                payment.getStatus(),
                payment.getTransactionId(),
                payment.getMethod(),
                payment.getMessage(),
                payment.getAmount()
        );
    }

    private static List<OrderItemRequest> mapItems(List<CheckoutItemRequest> items) {
        return items.stream().map(ci -> {
            OrderItemRequest r = new OrderItemRequest();
            r.setProductId(ci.getProductId());
            r.setQuantity(ci.getQuantity());
            // NO seteamos price: OrderService lo sobreescribe con precio de catálogo
            return r;
        }).toList();
    }
}
