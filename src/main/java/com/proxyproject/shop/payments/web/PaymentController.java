package com.proxyproject.shop.payments.web;

import com.proxyproject.shop.payments.app.PaymentService;
import com.proxyproject.shop.payments.domain.PaymentMethod;
import com.proxyproject.shop.payments.dto.PaymentRequest;
import com.proxyproject.shop.payments.dto.PaymentResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public PaymentResponse pay(@Valid @RequestBody PaymentRequest request) {
        return service.pay(request.getOrderId(), request.getMethod(), request.getAmount());
    }
}
