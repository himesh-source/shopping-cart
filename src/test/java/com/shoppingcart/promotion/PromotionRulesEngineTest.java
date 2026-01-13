package com.shoppingcart.promotion;

import com.shoppingcart.domain.CartItem;
import com.shoppingcart.domain.CartProduct;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PromotionRulesEngineTest {

    @Test
    void calculateTotalDiscount() {
        CartItem item = new CartItem(
                new CartProduct("cornflakes", new BigDecimal("2.52")), 2); // total 5.04

        PromotionRule rule1 = new DiscountPercentageRule("cornflakes", new BigDecimal("10")); // discount 0.50
        PromotionRule rule2 = new DiscountPercentageRule("cornflakes", new BigDecimal("50")); // discount 1.00

        PromotionRulesEngineImpl engine = new PromotionRulesEngineImpl(List.of(rule1, rule2));

        BigDecimal totalDiscount = engine.calculateTotalDiscount(List.of(item));

        // First rule wins
        assertEquals(new BigDecimal("0.504"), totalDiscount);
    }
}