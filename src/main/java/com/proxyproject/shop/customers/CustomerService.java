package com.proxyproject.shop.customers;

import com.proxyproject.shop.customers.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;



import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CustomerService {
  private final CustomerRepository repo;

  public CustomerService(CustomerRepository repo) { this.repo = repo; }

  public List<CustomerResponse> list() {
    return repo.findAll().stream().map(CustomerMapper::toResponse).toList();
  }

  public CustomerResponse get(Long id) {
    return repo.findById(id).map(CustomerMapper::toResponse)
      .orElseThrow(() -> new NoSuchElementException("Customer not found: " + id));
  }

  public CustomerResponse create(CustomerCreateRequest r) {
    if (repo.existsByEmail(r.email())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
    }
    Customer saved = repo.save(CustomerMapper.fromCreate(r));
    return CustomerMapper.toResponse(saved);
  }

  public CustomerResponse update(Long id, CustomerUpdateRequest r) {
    var db = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Customer not found: " + id));
    // If email changes, enforce uniqueness
    if (!db.getEmail().equalsIgnoreCase(r.email()) && repo.existsByEmail(r.email())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
    }
    CustomerMapper.applyUpdate(db, r);
    return CustomerMapper.toResponse(repo.save(db));
  }

  public void delete(Long id) {
    if (!repo.existsById(id)) throw new NoSuchElementException("Customer not found: " + id);
    repo.deleteById(id);
  }
}
