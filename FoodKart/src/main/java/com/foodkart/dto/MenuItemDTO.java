package com.foodkart.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemDTO {

    private String id;
    private String name;
    private double price;
    private boolean isAvailable;
}
