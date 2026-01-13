package com.shoppingcart.dto;

import java.math.BigDecimal;

public record DiscountDto(BigDecimal finalPrice, String description) {}

