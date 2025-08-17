package com.proxyproject.shop.checkout.web;

import com.proxyproject.shop.checkout.app.CheckoutFacade;
import com.proxyproject.shop.checkout.dto.CheckoutRequest;
import com.proxyproject.shop.checkout.dto.CheckoutResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private final CheckoutFacade facade;

    public CheckoutController(CheckoutFacade facade) {
        this.facade = facade;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public CheckoutResponse checkout(@Valid @RequestBody CheckoutRequest request) {
        return facade.checkout(request);
    }
}
