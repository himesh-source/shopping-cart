package com.shoppingcart.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Cart {

    // Thread-safe map for concurrent access
    private final Map<String, CartItem> items = new ConcurrentHashMap<>();

    /**
     * Add item to cart atomically.
     */
    public void addItem(CartProduct product, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");

        items.compute(product.name(), (key, existingItem) -> {
            if (existingItem == null) {
                return new CartItem(product, quantity);
            } else {
                existingItem.addQuantity(quantity); // CartItem itself is thread-safe
                return existingItem;
            }
        });
    }

    public Map<String, CartItem> getItems() {
        return items;
    }

}
