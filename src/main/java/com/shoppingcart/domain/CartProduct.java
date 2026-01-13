package com.shoppingcart.domain;

import java.math.BigDecimal;
import java.util.Objects;

import static com.shoppingcart.constants.CartConstants.MONEY_ROUNDING;
import static com.shoppingcart.constants.CartConstants.MONEY_SCALE;

public record CartProduct(String name, BigDecimal price) {
    public CartProduct {
        Objects.requireNonNull(name, "Product name must not be null");
        Objects.requireNonNull(price, "Price must not be null");

        if (name.isBlank()) {
            throw new IllegalArgumentException("Product name must not be blank");
        }

        if (price.signum() < 0) {
            throw new IllegalArgumentException("Price must be non-negative");
        }

        // Normalize price to a consistent scale
        price = price.setScale(MONEY_SCALE, MONEY_ROUNDING);
    }
}
