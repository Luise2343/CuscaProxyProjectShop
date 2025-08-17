package com.proxyproject.shop.orders.mapper;

import com.proxyproject.shop.orders.domain.Order;
import com.proxyproject.shop.orders.domain.OrderItem;
import com.proxyproject.shop.orders.dto.OrderItemResponse;
import com.proxyproject.shop.orders.dto.OrderResponse;

import java.util.List;

public final class OrderMapper {

    private OrderMapper() {}

    public static OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems()
                .stream()
                .map(OrderMapper::toItemResponse)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getTotal(),
                items
        );
    }

    private static OrderItemResponse toItemResponse(OrderItem it) {
        return new OrderItemResponse(
                it.getId(),
                it.getProductId(),
                it.getQuantity(),
                it.getPrice()
        );
    }
}
