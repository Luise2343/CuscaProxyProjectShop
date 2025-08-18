package com.proxyproject.shop.products.dto;

import java.math.BigDecimal;

public class ProductDto {
    private Long id;
    private String title;
    private BigDecimal price;

    public ProductDto() {}

    public ProductDto(Long id, String title, BigDecimal price) {
        this.id = id;
        this.title = title;
        this.price = price;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public BigDecimal getPrice() { return price; }

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
