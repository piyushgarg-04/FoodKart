package com.foodkart.dto;

import com.foodkart.enums.OrderStatus;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {

    private String orderId;

    private String restaurantId;

    private List<OrderItemDTO> items;

    private OrderStatus orderStatus;
}
