package com.proxyproject.shop.orders.web;

import com.proxyproject.shop.orders.dto.CreateOrderRequest;
import com.proxyproject.shop.orders.dto.OrderResponse;
import com.proxyproject.shop.orders.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    // Ambos roles pueden crear/listar (ajústalo si quieres más restricción)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
        return service.create(request);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable Long id) {
        return service.getById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping
    public List<OrderResponse> list() {
        return service.list();
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/{id}/cancel")
    public OrderResponse cancel(@PathVariable Long id) {
        return service.cancel(id);
    }
}
    