package com.shoppingcart.promotion;

import com.shoppingcart.domain.CartItem;
import com.shoppingcart.dto.DiscountDto;

import java.math.BigDecimal;
import java.util.Optional;


public class FreeItemPromotionalRule implements PromotionRule {
    private final String productName; // configurable
    private final int buyQuantity;
    private final int freeQuantity;


    public FreeItemPromotionalRule(String productName, int buyQuantity, int freeQuantity) {
        this.productName = productName;
        this.buyQuantity = buyQuantity;
        this.freeQuantity = freeQuantity;
    }



    @Override
    public BigDecimal discount(CartItem item) {
        if (!item.getProduct().name().equalsIgnoreCase(productName)) {
            return BigDecimal.ZERO;
        }

        int qty = item.getQuantity();
        int groupSize = buyQuantity + freeQuantity;

        int freeUnits = (qty / groupSize) * freeQuantity;

        return item.getProduct().price()
                .multiply(BigDecimal.valueOf(freeUnits));
    }

    @Override
    public Optional<DiscountDto> getDiscount(CartItem item) {
        if (!item.getProduct().name().equals(productName)) {
            return Optional.empty();
        }

        int qty = item.getQuantity();
        int group = buyQuantity + freeQuantity;

        int payable = (qty / group) * buyQuantity + (qty % group);

        BigDecimal finalPrice = item.getProduct().price()
                .multiply(BigDecimal.valueOf(payable));

        return Optional.of(new DiscountDto(finalPrice, "Buy " + buyQuantity + " Get " + freeQuantity));
    }

}
