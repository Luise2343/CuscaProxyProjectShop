package com.proxyproject.shop.customers;

import com.proxyproject.shop.customers.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

  private final CustomerService service;

  public CustomerController(CustomerService service) { this.service = service; }

  @GetMapping
  public List<CustomerResponse> list() {
    return service.list();
  }

  @GetMapping("/{id}")
  public CustomerResponse get(@PathVariable Long id) {
    return service.get(id);
  }

  @PostMapping
  public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerCreateRequest req) {
    var created = service.create(req);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @PutMapping("/{id}")
  public CustomerResponse update(@PathVariable Long id, @Valid @RequestBody CustomerUpdateRequest req) {
    return service.update(id, req);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}
