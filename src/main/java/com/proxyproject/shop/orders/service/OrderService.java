package com.proxyproject.shop.orders.service;

import com.proxyproject.shop.orders.domain.Order;
import com.proxyproject.shop.orders.domain.OrderItem;
import com.proxyproject.shop.orders.domain.OrderStatus;
import com.proxyproject.shop.orders.dto.CreateOrderRequest;
import com.proxyproject.shop.orders.dto.OrderItemRequest;
import com.proxyproject.shop.orders.dto.OrderResponse;
import com.proxyproject.shop.orders.mapper.OrderMapper;
import com.proxyproject.shop.orders.repo.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orders;

    public OrderService(OrderRepository orders) {
        this.orders = orders;
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Items are required");
        }

        Order order = new Order();
        order.setCustomerId(request.getCustomerId());

        // map items
        for (OrderItemRequest ir : request.getItems()) {
            if (ir.getQuantity() == null || ir.getQuantity() < 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item quantity must be >= 1");
            }
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(ir.getProductId());
            item.setQuantity(ir.getQuantity());
            item.setPrice(ir.getPrice());
            order.getItems().add(item);
        }

        // compute total
        order.setTotal(order.getItems().stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        Order saved = orders.save(order);
        return OrderMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponse getById(Long id) {
        Order order = orders.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        return OrderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> list() {
        return orders.findAll().stream().map(OrderMapper::toResponse).toList();
    }

    @Transactional
    public OrderResponse cancel(Long id) {
        Order order = orders.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (order.getStatus() == OrderStatus.PAID) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot cancel a PAID order");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            return OrderMapper.toResponse(order); // idempotente
        }

        order.setStatus(OrderStatus.CANCELLED);
        return OrderMapper.toResponse(order);
    }
}
