package com.shoppingcart.promotion;

import com.shoppingcart.domain.CartItem;
import com.shoppingcart.dto.DiscountDto;

import java.math.BigDecimal;
import java.util.Optional;

public interface PromotionRule {
    BigDecimal discount(CartItem item);
    Optional<DiscountDto> getDiscount(CartItem item);

}
