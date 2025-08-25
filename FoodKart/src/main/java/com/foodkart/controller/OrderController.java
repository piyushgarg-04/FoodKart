package com.foodkart.controller;

import com.foodkart.dto.OrderDTO;
import com.foodkart.entity.Order;
import com.foodkart.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> placeOrder(@Valid @RequestBody OrderDTO orderDTO){
        Order order = orderService.placeOrder(orderDTO);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{id}/dispatch")
    public ResponseEntity<Order> dispatchOrder(@PathVariable String id){
        Order order = orderService.dispatchOrder(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/dispatched")
    public ResponseEntity<List<Order>> getDispatchedOrders(){
        List<Order> dispatched = orderService.getDispatchedOrders();
        return ResponseEntity.ok(dispatched);
    }
}