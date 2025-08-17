package com.proxyproject.shop.payments.app;

import com.proxyproject.shop.orders.domain.Order;
import com.proxyproject.shop.orders.domain.OrderStatus;
import com.proxyproject.shop.orders.repo.OrderRepository;
import com.proxyproject.shop.payments.domain.PaymentMethod;
import com.proxyproject.shop.payments.domain.PaymentResult;
import com.proxyproject.shop.payments.dto.PaymentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
public class PaymentService {

    private final OrderRepository orders;
    private final PaymentStrategyFactory factory;

    public PaymentService(OrderRepository orders, PaymentStrategyFactory factory) {
        this.orders = orders;
        this.factory = factory;
    }

    @Transactional
    public PaymentResponse pay(Long orderId, PaymentMethod method, BigDecimal amount) {
        Order order = orders.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        // Reglas de estado
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Order is CANCELLED");
        }
        if (order.getStatus() == OrderStatus.PAID) {
            // idempotencia suave: devolver respuesta indicando ya está pagada
            return PaymentResponse.alreadyPaid(orderId);
        }

        // Validación de montos: debe coincidir con el total
        if (amount == null || order.getTotal() == null || amount.compareTo(order.getTotal()) != 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must equal order total");
        }

        PaymentResult result = factory.get(method).pay(order, amount);
        if (!result.isSuccess()) {
            throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED, "Payment failed: " + result.getMessage());
        }

        order.setStatus(OrderStatus.PAID);
        orders.save(order);

        return PaymentResponse.paid(orderId, result.getTransactionId(), method.name(), result.getMessage(), amount);
    }
}
