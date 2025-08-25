package com.foodkart.repository;

import com.foodkart.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, String> {

    List<MenuItem> findByRestaurantId(String restaurantId);
}