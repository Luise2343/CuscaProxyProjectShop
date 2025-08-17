package com.proxyproject.shop.payments.strategy;

import com.proxyproject.shop.orders.domain.Order;
import com.proxyproject.shop.payments.domain.PaymentMethod;
import com.proxyproject.shop.payments.domain.PaymentResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CreditCardPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentMethod supports() {
        return PaymentMethod.CREDIT_CARD;
    }

    @Override
    public PaymentResult pay(Order order, BigDecimal amount) {
        // Simulación: validaciones simples
        if (amount == null || amount.signum() <= 0) {
            return PaymentResult.fail("Invalid amount");
        }
        // Aquí iría la integración con pasarela real
        return PaymentResult.ok("CreditCard payment approved");
    }
}
