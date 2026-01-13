package com.shoppingcart.dto;

public record ProductRequestDto(String productName, int quantity) {
    public ProductRequestDto {
        if (productName == null || productName.isBlank())
            throw new IllegalArgumentException("Invalid productName");
        if (quantity <= 0)
            throw new IllegalArgumentException("Invalid quantity");
    }
}
