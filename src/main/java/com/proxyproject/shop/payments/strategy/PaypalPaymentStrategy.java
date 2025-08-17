package com.proxyproject.shop.payments.strategy;

import com.proxyproject.shop.orders.domain.Order;
import com.proxyproject.shop.payments.domain.PaymentMethod;
import com.proxyproject.shop.payments.domain.PaymentResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaypalPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentMethod supports() {
        return PaymentMethod.PAYPAL;
    }

    @Override
    public PaymentResult pay(Order order, BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            return PaymentResult.fail("Invalid amount");
        }
        return PaymentResult.ok("PayPal payment approved");
    }
}
