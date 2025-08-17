package com.proxyproject.shop.orders.repo;

import com.proxyproject.shop.orders.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
