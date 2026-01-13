package com.shoppingcart.repository;

import com.shoppingcart.promotion.PromotionRule;

import java.util.List;

public interface PromotionRepository {
    List<PromotionRule> findActivePromotions();
}
