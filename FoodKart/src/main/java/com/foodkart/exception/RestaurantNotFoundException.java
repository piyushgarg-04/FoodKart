package com.foodkart.exception;

public class RestaurantNotFoundException extends RuntimeException {
    public RestaurantNotFoundException(String id) {
        super("Restaurant not found: " + id);
    }
}
