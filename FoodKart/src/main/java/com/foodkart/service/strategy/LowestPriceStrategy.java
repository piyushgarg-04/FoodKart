package com.foodkart.service.strategy;

import com.foodkart.entity.Restaurant;
import com.foodkart.dto.OrderItemDTO;
import com.foodkart.repository.RestaurantRepository;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class LowestPriceStrategy implements RestaurantSelectionStrategy {

    private final RestaurantRepository restaurantRepository;

    public LowestPriceStrategy(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public Restaurant findRestaurant(List<OrderItemDTO> items) {
        // Find restaurant with lowest total price that has all items and capacity

        Optional<Restaurant> suitable = restaurantRepository.findAll().stream()
                .filter(rest -> rest.getCurrentOrdersCount() < rest.getProcessingCapacity())
                .filter(rest -> items.stream().allMatch(item ->
                        rest.getMenuItems().stream()
                                .anyMatch(mi -> mi.getId().equals(item.getMenuItemId()) && mi.isAvailable())))
                .min(Comparator.comparingDouble(rest -> calculateTotalPrice(rest, items)));

        return suitable.orElse(null);
    }

    private double calculateTotalPrice(Restaurant restaurant, List<OrderItemDTO> items) {
        double total = 0d;
        for (OrderItemDTO item : items) {
            total += restaurant.getMenuItems().stream()
                    .filter(mi -> mi.getId().equals(item.getMenuItemId()))
                    .findFirst()
                    .map(mi -> mi.getPrice() * item.getQuantity())
                    .orElse(Double.MAX_VALUE);
        }
        return total;
    }
}