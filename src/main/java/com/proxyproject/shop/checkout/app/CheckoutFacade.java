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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public CheckoutResponse checkout(CheckoutRequest request) {
        // 1) Crear la orden
        CreateOrderRequest createOrder = new CreateOrderRequest();
        createOrder.setCustomerId(request.getCustomerId());
        createOrder.setItems(mapItems(request.getItems()));

        OrderResponse order = orderService.create(createOrder);

        // 2) Determinar el amount a utilizar (si no viene, usamos total de la orden)
        BigDecimal amount = request.getAmount() != null ? request.getAmount() : order.getTotal();

        // 3) Pagar (PaymentService valida que amount == order.total)
        PaymentResponse payment = paymentService.pay(order.getId(), request.getPaymentMethod(), amount);

        // 4) Respuesta agregada
        return new CheckoutResponse(
                order.getId(),
                order.getCustomerId(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getTotal(),
                order.getItems(),
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
            r.setPrice(ci.getPrice());
            return r;
        }).toList();
    }
}
