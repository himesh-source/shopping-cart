package com.shoppingcart.domain;

import java.math.BigDecimal;

public class ReceiptLine {
    private final String product;
    private final int quantity;
    private final BigDecimal originalTotal;
    private final BigDecimal discount;

    public String getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getOriginalTotal() {
        return originalTotal;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public BigDecimal getFinalTotal() {
        return finalTotal;
    }

    public String getPromotionDescription() {
        return promotionDescription;
    }

    private final BigDecimal finalTotal;
    private final String promotionDescription;

    public ReceiptLine(String product,
                       int quantity,
                       BigDecimal originalTotal,
                       BigDecimal discount,
                       BigDecimal finalTotal,
                       String promotionDescription) {
        this.product = product;
        this.quantity = quantity;
        this.originalTotal = originalTotal;
        this.discount = discount;
        this.finalTotal = finalTotal;
        this.promotionDescription = promotionDescription;
    }

    // getters
}
