package com.shoppingcart.service;

import java.math.BigDecimal;

public interface PriceService {
    /**
     * Retrieves the price of a product by its name.
     *
     * @param productName the product name, must not be null or blank
     * @return the price of the product (non-null, >= 0)
     * @throws IllegalArgumentException if productName is null or blank
     * @throws RuntimeException for unexpected errors (e.g., network or I/O failure)
     */
    BigDecimal getPrice(String productName);

}
