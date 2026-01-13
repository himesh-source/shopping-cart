package com.shoppingcart.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class CartItemTest {

    private CartProduct product;
    private CartItem item;

    @BeforeEach
    void setUp() {
        product = new CartProduct("cornflakes", new BigDecimal("2.52"));
        item = new CartItem(product, 1);
    }

    @Test
    void testTotalCalculation() {
        item.addQuantity(1);
        assertEquals(new BigDecimal("5.04").setScale(2), item.total().setScale(2));
    }

    @Test
    void testAddQuantityInvalid() {
        assertThrows(IllegalArgumentException.class, () -> item.addQuantity(0));
        assertThrows(IllegalArgumentException.class, () -> item.addQuantity(-1));
    }

    @Test
    void testCartItemTotalCalculation() {
        CartItem testItem = new CartItem(product, 2);
        assertEquals(new BigDecimal("5.04").setScale(2), testItem.total().setScale(2));
    }

    @Test
    void testInvalidQuantityThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new CartItem(product, 0));
        assertThrows(IllegalArgumentException.class, () -> new CartItem(product, -5));
    }

    @Test
    void testGetProductAndQuantity() {
        assertSame(product, item.getProduct());
        assertEquals(1, item.getQuantity());
        item.addQuantity(2);
        assertEquals(3, item.getQuantity());
    }

    @Test
    void testThreadSafetyOfAddQuantity() throws InterruptedException {
        int threads = 10;
        int incrementsPerThread = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    item.addQuantity(1);
                }
                latch.countDown();
            });
        }

        latch.await();
        assertEquals(1 + threads * incrementsPerThread, item.getQuantity());
        executor.shutdown();
    }
}