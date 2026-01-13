package com.shoppingcart.promotion;

import com.shoppingcart.domain.CartItem;
import com.shoppingcart.dto.DiscountDto;

import java.math.BigDecimal;
import java.util.Optional;

public class DiscountPercentageRule implements PromotionRule {
    private final String productName;
    private final BigDecimal percentage;

    public DiscountPercentageRule(String productName, BigDecimal percentage) {
        this.productName = productName;
        this.percentage = percentage;
    }

    @Override
    public BigDecimal discount(CartItem item) {
        if (!item.getProduct().name().equalsIgnoreCase(productName)) {
            return BigDecimal.ZERO;
        }

        return item.total()
                .multiply(percentage)
                .divide(BigDecimal.valueOf(100));
    }

    @Override
    public Optional<DiscountDto> getDiscount(CartItem item) {
            if (!item.getProduct().name().equals(productName)) {
                return Optional.empty();
            }

            BigDecimal original = item.total();
            BigDecimal discount = original.multiply(percentage).divide(new BigDecimal("100"));
            BigDecimal finalPrice = original.subtract(discount);

            return Optional.of(new DiscountDto(finalPrice, percentage + "% off"));
        }

}
