package com.foodkart.controller;

import com.foodkart.dto.MenuItemDTO;
import com.foodkart.dto.RestaurantDTO;
import com.foodkart.entity.Restaurant;
import com.foodkart.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService){
        this.restaurantService = restaurantService;
    }

    @PostMapping
    public ResponseEntity<Restaurant> addRestaurant(@Valid @RequestBody RestaurantDTO dto){
        Restaurant created = restaurantService.addRestaurant(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}/capacity")
    public ResponseEntity<Restaurant> updateCapacity(@PathVariable String id, @RequestParam int capacity){
        Restaurant updated = restaurantService.updateProcessingCapacity(id, capacity);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/menu")
    public ResponseEntity<Restaurant> updateMenu(@PathVariable String id, @RequestBody List<MenuItemDTO> menuItems){
        Restaurant updated = restaurantService.updateMenuItems(id, menuItems);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants(){
        return ResponseEntity.ok(restaurantService.findAll());
    }
}