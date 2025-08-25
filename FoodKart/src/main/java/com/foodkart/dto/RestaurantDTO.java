package com.foodkart.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantDTO {

    private String id;

    private String name;

    private String location;

    private int processingCapacity;

    private List<MenuItemDTO> menuItems;
}
