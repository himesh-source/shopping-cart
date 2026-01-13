package com.shoppingcart.dto;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

public record CartSummaryDto(Map<String, Integer> items, BigDecimal subtotal, BigDecimal tax, BigDecimal total) {
    public CartSummaryDto(Map<String, Integer> items,
                          BigDecimal subtotal,
                          BigDecimal tax,
                          BigDecimal total) {

        this.items = Map.copyOf(Objects.requireNonNull(items, "items"));
        this.subtotal = Objects.requireNonNull(subtotal, "subtotal");
        this.tax = Objects.requireNonNull(tax, "tax");
        this.total = Objects.requireNonNull(total, "total");
    }

}
