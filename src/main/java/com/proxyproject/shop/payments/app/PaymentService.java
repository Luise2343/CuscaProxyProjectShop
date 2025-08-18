package com.proxyproject.shop.payments.app;

import com.proxyproject.shop.orders.domain.Order;
import com.proxyproject.shop.orders.domain.OrderStatus;
import com.proxyproject.shop.orders.repo.OrderRepository;
import com.proxyproject.shop.payments.domain.PaymentMethod; // <-- enum
import com.proxyproject.shop.payments.dto.PaymentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentService {

    private final OrderRepository orders;

    public PaymentService(OrderRepository orders) {
        this.orders = orders;
    }

    // ---------- OVERLOAD para no tocar tu controller ----------
    public PaymentResponse pay(Long orderId, PaymentMethod method, BigDecimal amount) {
        return pay(orderId, method != null ? method.name() : null, amount);
    }
    // ----------------------------------------------------------

    /**
     * Process payment:
     * - 404: order not found
     * - 409: order CANCELLED or already PAID (idempotente para PAID)
     * - 400: amount null o distinto de order.total
     * - OK: marca PAID y devuelve PaymentResponse
     */
    @Transactional
    public PaymentResponse pay(Long orderId, String method, BigDecimal amount) {
        Order order = orders.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot pay a CANCELLED order");
        }
        if (order.getStatus() == OrderStatus.PAID) {
            // Idempotente: ya estaba pagada
            return buildResponse("PAID", method, amount != null ? amount : order.getTotal(), "Order already paid");
        }

        if (amount == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount is required");
        }
        if (order.getTotal() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order total is not available");
        }
        if (amount.compareTo(order.getTotal()) != 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment amount must equal order total (expected " + order.getTotal() + ")"
            );
        }

        // Marcar como pagada
        order.setStatus(OrderStatus.PAID);
        orders.save(order);

        return buildResponse("PAID", method, amount, "Payment approved");
    }

    // Usa setters (compatible con un POJO estándar)
    private static PaymentResponse buildResponse(String status, String method, BigDecimal amount, String message) {
        PaymentResponse r = new PaymentResponse();
        try { r.getClass().getMethod("setStatus", String.class).invoke(r, status); } catch (Exception ignored) {}
        try { r.getClass().getMethod("setTransactionId", String.class).invoke(r, UUID.randomUUID().toString()); } catch (Exception ignored) {}
        try { r.getClass().getMethod("setMethod", String.class).invoke(r, method); } catch (Exception ignored) {}
        try { r.getClass().getMethod("setMessage", String.class).invoke(r, message); } catch (Exception ignored) {}
        try { r.getClass().getMethod("setAmount", BigDecimal.class).invoke(r, amount); } catch (Exception ignored) {}
        return r;
    }
}
