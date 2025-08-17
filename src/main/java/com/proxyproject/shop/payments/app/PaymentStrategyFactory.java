package com.proxyproject.shop.payments.app;

import com.proxyproject.shop.payments.domain.PaymentMethod;
import com.proxyproject.shop.payments.strategy.PaymentStrategy;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class PaymentStrategyFactory {

    private final Map<PaymentMethod, PaymentStrategy> strategies = new EnumMap<>(PaymentMethod.class);

    public PaymentStrategyFactory(List<PaymentStrategy> strategyList) {
        for (PaymentStrategy s : strategyList) {
            strategies.put(s.supports(), s);
        }
    }

    public PaymentStrategy get(PaymentMethod method) {
        PaymentStrategy s = strategies.get(method);
        if (s == null) {
            throw new IllegalArgumentException("Unsupported payment method: " + method);
        }
        return s;
    }
}
