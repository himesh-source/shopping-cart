package com.shoppingcart.domain;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

public class CartItem {
    private final CartProduct product;
    private final AtomicInteger quantity;

    public CartItem(CartProduct product, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.product = product;
        this.quantity = new AtomicInteger(quantity);
    }

    public void addQuantity(int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        quantity.addAndGet(qty);
    }

    public BigDecimal total() {
        return product.price()
                .multiply(BigDecimal.valueOf(quantity.get()));
    }

    public CartProduct getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity.get();
    }
}
