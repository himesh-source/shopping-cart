package com.shoppingcart.service;

import com.shoppingcart.receipt.Receipt;
import com.shoppingcart.dto.CartSummaryDto;
import com.shoppingcart.dto.ProductRequestDto;

public interface CartService {
    /**
     * Adds the specified product and quantity to the cart.
     *
     * @param request product name and quantity
     * @throws IllegalArgumentException if the request is invalid
     * @throws RuntimeException if price retrieval or cart update fails
     */
    void addProduct(ProductRequestDto request);

    /**
     * Returns a snapshot of the cart summary.
     * The returned object is immutable and thread-safe.
     *
     * @return cart summary
     */
    CartSummaryDto getSummary();

    public Receipt getReceipt();

}
