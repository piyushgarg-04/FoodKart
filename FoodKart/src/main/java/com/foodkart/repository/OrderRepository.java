package com.foodkart.repository;

import com.foodkart.entity.Order;
import com.foodkart.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findByOrderStatus(OrderStatus status);
}
