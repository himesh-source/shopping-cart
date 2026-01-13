package com.shoppingcart.promotion;

import com.shoppingcart.domain.CartItem;
import com.shoppingcart.dto.DiscountDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class PromotionRulesEngineImpl implements PromotionRulesEngine{
    private final List<PromotionRule> rules;

    public PromotionRulesEngineImpl(List<PromotionRule> rules) {
        this.rules = rules;
    }

    public BigDecimal calculateTotalDiscount(List<CartItem> items) {

        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (CartItem item : items) {

            for (PromotionRule rule : rules) {

                BigDecimal discount = rule.discount(item);

                if (discount.signum() > 0) {
                    totalDiscount = totalDiscount.add(discount);
                    break; // one rule per item. Break out of inner for loop
                }
            }
        }

        return totalDiscount;
    }


    public Optional<DiscountDto> getDiscount(CartItem item) {
        for (PromotionRule rule : rules) {
            Optional<DiscountDto> result = rule.getDiscount(item);
            if (result.isPresent()) {
                return Optional.of(result.get()); // first match wins
            }
        }
        return Optional.of(new DiscountDto(item.total(), "No promotion"));
    }
}
