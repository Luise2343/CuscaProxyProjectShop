package com.proxyproject.shop.payments.strategy;

import com.proxyproject.shop.orders.domain.Order;
import com.proxyproject.shop.payments.domain.PaymentMethod;
import com.proxyproject.shop.payments.domain.PaymentResult;

import java.math.BigDecimal;

public interface PaymentStrategy {
    PaymentMethod supports();
    PaymentResult pay(Order order, BigDecimal amount);
}
