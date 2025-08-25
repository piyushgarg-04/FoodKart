package com.foodkart.repository;

import com.foodkart.entity.ServedItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServedItemRepository extends JpaRepository<ServedItem, Long> {
    Optional<ServedItem> findByRestaurantIdAndMenuItemId(String restaurantId, String menuItemId);
}
