package com.proxyproject.shop.customers.dto;

import com.proxyproject.shop.customers.Customer;

public record CustomerResponse(
  Long id, String firstName, String lastName, String email, Customer.Status status
) {}
