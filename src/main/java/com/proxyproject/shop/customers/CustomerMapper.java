package com.proxyproject.shop.customers;

import com.proxyproject.shop.customers.dto.*;

public class CustomerMapper {

  public static CustomerResponse toResponse(Customer c) {
    return new CustomerResponse(c.getId(), c.getFirstName(), c.getLastName(), c.getEmail(), c.getStatus());
  }

  public static Customer fromCreate(CustomerCreateRequest r) {
    Customer c = new Customer();
    c.setFirstName(r.firstName());
    c.setLastName(r.lastName());
    c.setEmail(r.email());
    c.setStatus(r.status());
    return c;
  }

  public static void applyUpdate(Customer c, CustomerUpdateRequest r) {
    c.setFirstName(r.firstName());
    c.setLastName(r.lastName());
    c.setEmail(r.email());
    c.setStatus(r.status());
  }
}
