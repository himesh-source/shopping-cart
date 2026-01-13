package com.shoppingcart.constants;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class CartConstants {
    private CartConstants() {
        throw new AssertionError("Utility class");
    }

    // Configuration
    public static final String PRICE_API_BASE_URL =
            "https://equalexperts.github.io/backend-take-home-test-data";

    public static final BigDecimal TAX_RATE = BigDecimal.valueOf(0.125);

    public static final int MONEY_SCALE = 2;

    public static final RoundingMode MONEY_ROUNDING = RoundingMode.HALF_UP;
}
