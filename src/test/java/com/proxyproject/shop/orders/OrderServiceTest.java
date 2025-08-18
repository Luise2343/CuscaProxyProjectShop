package com.proxyproject.shop.orders;

import com.proxyproject.shop.orders.domain.Order;
import com.proxyproject.shop.orders.domain.OrderItem;
import com.proxyproject.shop.orders.dto.CreateOrderRequest;
import com.proxyproject.shop.orders.dto.OrderItemRequest;
import com.proxyproject.shop.orders.pricing.OrderPricingService;
import com.proxyproject.shop.orders.repo.OrderRepository;
import com.proxyproject.shop.orders.service.OrderService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orders;
    @Mock private OrderPricingService pricing;

    @InjectMocks private OrderService service;

    @Captor private ArgumentCaptor<Order> orderCaptor;

    @Test
    @DisplayName("create() must ignore client prices and persist total from pricing service")
    void createShouldIgnoreClientPricesAndUsePricingTotal() {
        // Arrange
        Long productId = 1L;
        int qty = 2;

        // El pricing calcula el total con precio de catálogo y escala 2
        when(pricing.applyCatalogPricesAndReturnTotal(anyList()))
                .thenReturn(new BigDecimal("219.90"));

        // save(...) devuelve el mismo Order que recibe (suficiente para el test unitario)
        when(orders.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        // DTO de entrada: incluye price (que debe ser ignorado)
        OrderItemRequest itemReq = new OrderItemRequest();
        itemReq.setProductId(productId);
        itemReq.setQuantity(qty);
        itemReq.setPrice(new BigDecimal("0.01")); // este valor NO debe afectar el total

        CreateOrderRequest req = new CreateOrderRequest();
        req.setCustomerId(1L);
        req.setItems(List.of(itemReq));

        // Act
        service.create(req);

        // Assert
        verify(orders).save(orderCaptor.capture());
        Order saved = orderCaptor.getValue();

        assertEquals(new BigDecimal("219.90"), saved.getTotal(),
                "Order.total must come from OrderPricingService, not from client-provided prices");

        // El servicio debe haber llamado al pricing una sola vez con los ítems construidos
        verify(pricing, times(1)).applyCatalogPricesAndReturnTotal(anyList());
        verifyNoMoreInteractions(pricing);
    }
}
