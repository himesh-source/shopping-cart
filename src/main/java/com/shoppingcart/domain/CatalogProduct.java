package com.shoppingcart.domain;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static com.shoppingcart.constants.CartConstants.MONEY_ROUNDING;
import static com.shoppingcart.constants.CartConstants.MONEY_SCALE;

public class CatalogProduct {
    private final String name;
    private final AtomicReference<BigDecimal> price;

    public CatalogProduct(String name, BigDecimal price) {
        Objects.requireNonNull(name, "Product name must not be null");
        Objects.requireNonNull(price, "Price must not be null");

        if (name.isBlank()) {
            throw new IllegalArgumentException("Product name must not be blank");
        }
        if (price.signum() < 0) {
            throw new IllegalArgumentException("Price must be non-negative");
        }

        this.name = name;
        this.price = new AtomicReference<>(
                price.setScale(MONEY_SCALE, MONEY_ROUNDING)
        );
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price.get();
    }


    public void changePrice(BigDecimal newPrice) {
        Objects.requireNonNull(newPrice, "Price must not be null");

        if (newPrice.signum() < 0) {
            throw new IllegalArgumentException("Price must be non-negative");
        }

        price.set(newPrice.setScale(MONEY_SCALE, MONEY_ROUNDING));
    }
}
