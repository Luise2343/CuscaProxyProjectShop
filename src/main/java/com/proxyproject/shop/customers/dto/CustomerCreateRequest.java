package com.proxyproject.shop.customers.dto;

import com.proxyproject.shop.customers.Customer;
import jakarta.validation.constraints.*;

public record CustomerCreateRequest(
  @NotBlank String firstName,
  @NotBlank String lastName,
  @Email @NotBlank String email,
  @NotNull Customer.Status status
) {}
