package com.shoppingcart.service;

import com.shoppingcart.domain.Cart;
import com.shoppingcart.domain.CartItem;
import com.shoppingcart.domain.CartProduct;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.shoppingcart.constants.CartConstants.TAX_RATE;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class CartIntegrationTest {
    @Test
    void addItemAndComputeTotals() {
        Cart cart = new Cart();

        CartProduct cornflakes = new CartProduct("cornflakes", BigDecimal.valueOf(2.52));
        CartProduct weetabix = new CartProduct("weetabix", BigDecimal.valueOf(9.98));

        cart.addItem(cornflakes, 1);
        cart.addItem(cornflakes, 1); // second add
        cart.addItem(weetabix, 1);

        Map<String, CartItem> items = cart.getItems();

        assertEquals(2, items.get("cornflakes").getQuantity());
        assertEquals(1, items.get("weetabix").getQuantity());

        // Subtotal
        BigDecimal subtotal = items.values().stream()
                .map(CartItem::total)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertEquals(BigDecimal.valueOf(15.02).setScale(2), subtotal.setScale(2));

        // Tax (12.5%)
        BigDecimal tax = subtotal.multiply(TAX_RATE).setScale(2, BigDecimal.ROUND_HALF_UP);

        // Total
        BigDecimal total = subtotal.add(tax).setScale(2, BigDecimal.ROUND_HALF_UP);

        assertEquals(BigDecimal.valueOf(1.88).setScale(2), tax);   // 15.02 × 0.125 = 1.8775 → 1.88
        assertEquals(BigDecimal.valueOf(16.90).setScale(2), total); // 15.02 + 1.88 = 16.90

    }

    @Test
    void addItemConcurrent() throws InterruptedException {
        Cart cart = new Cart();
        CartProduct product = new CartProduct("frosties", BigDecimal.valueOf(3.0));

        int threads = 10;
        int addsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        Runnable addTask = () -> {
            for (int i = 0; i < addsPerThread; i++) {
                cart.addItem(product, 1);
            }
            latch.countDown();
        };

        for (int i = 0; i < threads; i++) {
            executor.submit(addTask);
        }

        latch.await();
        executor.shutdown();

        int expectedQuantity = threads * addsPerThread;
        assertEquals(expectedQuantity, cart.getItems().get("frosties").getQuantity());
    }
}

