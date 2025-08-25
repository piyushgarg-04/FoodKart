package com.foodkart.service;

import com.foodkart.dto.MenuItemDTO;
import com.foodkart.dto.RestaurantDTO;
import com.foodkart.entity.MenuItem;
import com.foodkart.entity.Restaurant;
import com.foodkart.exception.RestaurantNotFoundException;
import com.foodkart.repository.MenuItemRepository;
import com.foodkart.repository.RestaurantRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    public RestaurantService(RestaurantRepository restaurantRepository,
                             MenuItemRepository menuItemRepository) {
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public Restaurant addRestaurant(RestaurantDTO dto) {
        Restaurant restaurant = Restaurant.builder()
                .id(dto.getId() == null ? UUID.randomUUID().toString() : dto.getId())
                .name(dto.getName())
                .location(dto.getLocation())
                .processingCapacity(dto.getProcessingCapacity())
                .currentOrdersCount(0)
                .build();

        Restaurant saved = restaurantRepository.save(restaurant);

        if (dto.getMenuItems() != null && !dto.getMenuItems().isEmpty()) {
            List<MenuItem> menuItems = dto.getMenuItems().stream()
                    .map(this::dtoToEntity)
                    .peek(mi -> mi.setRestaurant(saved))
                    .collect(Collectors.toList());
            menuItemRepository.saveAll(menuItems);
            saved.setMenuItems(menuItems);
        }

        return saved;
    }

    @Transactional
    public Restaurant updateProcessingCapacity(String restaurantId, int newCapacity) {
        Restaurant restaurant = getRestaurantByIdOrThrow(restaurantId);
        restaurant.setProcessingCapacity(newCapacity);
        return restaurantRepository.save(restaurant);
    }

    @Transactional
    public Restaurant updateMenuItems(String restaurantId, List<MenuItemDTO> menuItemDTOs) {
        Restaurant restaurant = getRestaurantByIdOrThrow(restaurantId);

        for (MenuItemDTO dto : menuItemDTOs) {
            Optional<MenuItem> menuItemOpt = restaurant.getMenuItems()
                    .stream()
                    .filter(mi -> mi.getId().equals(dto.getId()))
                    .findFirst();

            MenuItem menuItem;
            if (menuItemOpt.isPresent()) {
                menuItem = menuItemOpt.get();
                menuItem.setName(dto.getName());
                menuItem.setPrice(dto.getPrice());
                menuItem.setAvailable(dto.isAvailable());
            } else {
                menuItem = dtoToEntity(dto);
                menuItem.setRestaurant(restaurant);
                restaurant.getMenuItems().add(menuItem);
            }
            menuItemRepository.save(menuItem);
        }
        return restaurantRepository.save(restaurant);
    }

    public boolean areItemsAvailable(Restaurant restaurant, List<com.foodkart.dto.OrderItemDTO> orderItems) {
        for (com.foodkart.dto.OrderItemDTO orderItem : orderItems) {
            Optional<MenuItem> menuItem = restaurant.getMenuItems().stream()
                    .filter(mi -> mi.getId().equals(orderItem.getMenuItemId()) && mi.isAvailable())
                    .findFirst();
            if (menuItem.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public Restaurant save(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    public Optional<Restaurant> findById(String id) {
        return restaurantRepository.findById(id);
    }

    public List<Restaurant> findAll() {
        return restaurantRepository.findAll();
    }

    // Private helper method to fetch Restaurant or throw nicely
    private Restaurant getRestaurantByIdOrThrow(String restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
    }

    private MenuItem dtoToEntity(MenuItemDTO dto) {
        return MenuItem.builder()
                .id(dto.getId() == null ? UUID.randomUUID().toString() : dto.getId())
                .name(dto.getName())
                .price(dto.getPrice())
                .isAvailable(dto.isAvailable())
                .build();
    }
}