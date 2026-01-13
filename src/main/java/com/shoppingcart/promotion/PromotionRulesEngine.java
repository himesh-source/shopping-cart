package com.shoppingcart.promotion;

import com.shoppingcart.domain.CartItem;
import com.shoppingcart.dto.DiscountDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PromotionRulesEngine {
    BigDecimal calculateTotalDiscount(List<CartItem> items);
    Optional<DiscountDto> getDiscount(CartItem item);
}
