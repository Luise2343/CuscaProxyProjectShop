package com.proxyproject.shop.products;

import com.proxyproject.shop.products.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "products",
        url = "${products.api.base-url}",
        path = "/api/products"
)
public interface ProductClient {

    @GetMapping("/{id}")
    ProductDto getById(@PathVariable("id") Long id);
}
