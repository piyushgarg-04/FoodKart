package com.foodkart.service;

import com.foodkart.dto.OrderDTO;
import com.foodkart.dto.OrderItemDTO;
import com.foodkart.entity.Order;
import com.foodkart.entity.OrderItem;
import com.foodkart.entity.Restaurant;
import com.foodkart.enums.OrderStatus;
import com.foodkart.repository.OrderItemRepository;
import com.foodkart.repository.OrderRepository;
import com.foodkart.service.strategy.RestaurantSelectionStrategy;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestaurantService restaurantService;
    private final RestaurantSelectionStrategy selectionStrategy;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        RestaurantService restaurantService,
                        RestaurantSelectionStrategy selectionStrategy) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.restaurantService = restaurantService;
        this.selectionStrategy = selectionStrategy;
    }

    @Transactional
    public Order placeOrder(OrderDTO dto) {
        Restaurant restaurant;
        if (dto.getRestaurantId() != null) {
            restaurant = restaurantService.findById(dto.getRestaurantId())
                    .orElseThrow(() -> new RuntimeException("Restaurant not found: " + dto.getRestaurantId()));
        } else {
            restaurant = selectionStrategy.findRestaurant(dto.getItems());
            if (restaurant == null) {
                throw new RuntimeException("No suitable restaurant found for order");
            }
        }

        if (restaurant.getCurrentOrdersCount() >= restaurant.getProcessingCapacity()) {
            throw new RuntimeException("Restaurant capacity exceeded");
        }

        if (!restaurantService.areItemsAvailable(restaurant, dto.getItems())) {
            throw new RuntimeException("One or more items are unavailable");
        }

        int totalItems = dto.getItems().stream().mapToInt(OrderItemDTO::getQuantity).sum();
        double totalPrice = calculateTotalPrice(restaurant, dto.getItems());

        Order order = Order.builder()
                .id(dto.getOrderId() == null ? UUID.randomUUID().toString() : dto.getOrderId())
                .restaurant(restaurant)
                .itemCount(totalItems)
                .totalPrice(totalPrice)
                .orderStatus(OrderStatus.ACCEPTED)
                .build();

        List<OrderItem> orderItems = dto.getItems().stream()
                .map(itemDTO -> {
                    OrderItem oi = OrderItem.builder()
                            .menuItemId(itemDTO.getMenuItemId())
                            .quantity(itemDTO.getQuantity())
                            .order(order)
                            .build();
                    return oi;
                })
                .collect(Collectors.toList());

        order.setOrderItems(orderItems);

        orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);

        restaurant.setCurrentOrdersCount(restaurant.getCurrentOrdersCount() + 1);
        restaurantService.save(restaurant);

        return order;
    }

    @Transactional
    public Order dispatchOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (order.getOrderStatus() != OrderStatus.ACCEPTED) {
            throw new RuntimeException("Order is not in ACCEPTED state");
        }

        order.setOrderStatus(OrderStatus.DISPATCHED);
        orderRepository.save(order);

        Restaurant restaurant = order.getRestaurant();
        restaurant.setCurrentOrdersCount(restaurant.getCurrentOrdersCount() - 1);
        restaurantService.save(restaurant);

        // Update served items counts (optional)

        return order;
    }

    public List<Order> getDispatchedOrders() {
        return orderRepository.findByOrderStatus(OrderStatus.DISPATCHED);
    }

    private double calculateTotalPrice(Restaurant restaurant, List<OrderItemDTO> items) {
        double total = 0d;
        for (OrderItemDTO item : items) {
            double price = restaurant.getMenuItems().stream()
                    .filter(mi -> mi.getId().equals(item.getMenuItemId()))
                    .findFirst()
                    .map(mi -> mi.getPrice())
                    .orElseThrow(() -> new RuntimeException("Menu item not found: " + item.getMenuItemId()));
            total += price * item.getQuantity();
        }
        return total;
    }
}
