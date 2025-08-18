package com.proxyproject.shop.payments;

import com.proxyproject.shop.orders.domain.Order;
import com.proxyproject.shop.orders.domain.OrderStatus;
import com.proxyproject.shop.orders.repo.OrderRepository;
import com.proxyproject.shop.payments.app.PaymentService;
import com.proxyproject.shop.payments.domain.PaymentMethod;
import com.proxyproject.shop.payments.dto.PaymentResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock private OrderRepository orders;
    @InjectMocks private PaymentService service;

    @Captor private ArgumentCaptor<Order> orderCaptor;

    private static Order newOrder(Long id, OrderStatus status, BigDecimal total) {
        Order o = new Order();
        // id no es necesario para la lógica; el repo retorna por findById. Si tu setId no existe, omite.
        try { Order.class.getDeclaredMethod("setId", Long.class).invoke(o, id); } catch (Exception ignored) {}
        o.setStatus(status);
        o.setTotal(total);
        return o;
    }

    @Test
    @DisplayName("400 when amount is null")
    void should400WhenAmountIsNull() {
        Order o = newOrder(1L, OrderStatus.CREATED, new BigDecimal("10.00"));
        when(orders.findById(1L)).thenReturn(Optional.of(o));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.pay(1L, PaymentMethod.CREDIT_CARD, null));

        assertEquals(400, ex.getStatusCode().value());
        verify(orders, never()).save(any());
    }

    @Test
    @DisplayName("400 when amount != order.total")
    void should400WhenAmountDiffersFromTotal() {
        Order o = newOrder(1L, OrderStatus.CREATED, new BigDecimal("10.00"));
        when(orders.findById(1L)).thenReturn(Optional.of(o));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.pay(1L, PaymentMethod.CREDIT_CARD, new BigDecimal("9.99")));

        assertEquals(400, ex.getStatusCode().value());
        verify(orders, never()).save(any());
    }

    @Test
    @DisplayName("409 when order is CANCELLED")
    void should409WhenOrderIsCancelled() {
        Order o = newOrder(2L, OrderStatus.CANCELLED, new BigDecimal("50.00"));
        when(orders.findById(2L)).thenReturn(Optional.of(o));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.pay(2L, PaymentMethod.PAYPAL, new BigDecimal("50.00")));

        assertEquals(409, ex.getStatusCode().value());
        verify(orders, never()).save(any());
    }

    @Test
    @DisplayName("Idempotent PAID: returns OK without saving")
    void shouldReturnPaidWhenAlreadyPaidAndNotSave() {
        Order o = newOrder(3L, OrderStatus.PAID, new BigDecimal("219.90"));
        when(orders.findById(3L)).thenReturn(Optional.of(o));

        // amount null → el servicio usa order.total en la respuesta
        PaymentResponse resp = service.pay(3L, PaymentMethod.CREDIT_CARD, null);

        assertNotNull(resp);
        assertEquals("PAID", resp.getStatus());
        assertEquals("CREDIT_CARD", resp.getMethod());
        assertEquals(new BigDecimal("219.90"), resp.getAmount());
        // No debe persistir cambios (idempotente)
        verify(orders, never()).save(any());
    }

    @Test
    @DisplayName("Happy path: marks order as PAID and saves")
    void shouldMarkPaidAndSave() {
        Order o = newOrder(4L, OrderStatus.CREATED, new BigDecimal("219.90"));
        when(orders.findById(4L)).thenReturn(Optional.of(o));
        when(orders.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        PaymentResponse resp = service.pay(4L, PaymentMethod.CREDIT_CARD, new BigDecimal("219.90"));

        assertNotNull(resp);
        assertEquals("PAID", resp.getStatus());
        assertEquals("CREDIT_CARD", resp.getMethod());
        assertEquals(new BigDecimal("219.90"), resp.getAmount());

        verify(orders).save(orderCaptor.capture());
        Order saved = orderCaptor.getValue();
        assertEquals(OrderStatus.PAID, saved.getStatus(), "Order must be marked as PAID before saving");
    }

    @Test
    @DisplayName("404 when order not found")
    void should404WhenOrderNotFound() {
        when(orders.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.pay(99L, PaymentMethod.CRYPTO, new BigDecimal("1.00")));

        assertEquals(404, ex.getStatusCode().value());
        verify(orders, never()).save(any());
    }
}
