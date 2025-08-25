package com.foodkart.service.strategy;

import com.foodkart.entity.Restaurant;
import com.foodkart.dto.OrderItemDTO;

import java.util.List;

public interface RestaurantSelectionStrategy {
    Restaurant findRestaurant(List<OrderItemDTO> items);
}
