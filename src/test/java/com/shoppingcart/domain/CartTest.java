package com.shoppingcart.domain;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CartTest {

    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = new Cart();
    }

    @Test
    void testAddNewItem_cheerios() {
        CartProduct cheerios = new CartProduct("cheerios", new BigDecimal("2.62"));
        cart.addItem(cheerios, 2);

        Map<String, CartItem> items = cart.getItems();
        assertTrue(items.containsKey("cheerios"));
        assertEquals(2, items.get("cheerios").getQuantity());
    }

    @Test
    void testAddMultipleDifferentProducts() {
        cart.addItem(new CartProduct("cornflakes",new BigDecimal("2.52")), 1);
        cart.addItem(new CartProduct("frosties", new BigDecimal("3.52")), 3);
        cart.addItem(new CartProduct("shreddies", new BigDecimal("3.98")), 5);
        cart.addItem(new CartProduct("weetabix", new BigDecimal("9.98")), 2);

        Map<String, CartItem> items = cart.getItems();
        assertEquals(4, items.size());
        assertEquals(1, items.get("cornflakes").getQuantity());
        assertEquals(3, items.get("frosties").getQuantity());
        assertEquals(5, items.get("shreddies").getQuantity());
        assertEquals(2, items.get("weetabix").getQuantity());
    }

    @Test
    void testAddQuantityToExistingItem() {
        CartProduct cheerios = new CartProduct("cheerios", new BigDecimal("2.62"));
        cart.addItem(cheerios, 2);
        cart.addItem(cheerios, 3); // add more

        Map<String, CartItem> items = cart.getItems();
        assertEquals(5, items.get("cheerios").getQuantity());
    }

    @Test
    void testInvalidQuantityThrowsException() {
        CartProduct cornflakes = new CartProduct("cornflakes", new BigDecimal("2.52"));
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(cornflakes, 0));
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(cornflakes, -5));
    }

    @Test
    void testCartInitiallyEmpty() {
        assertTrue(cart.getItems().isEmpty());
    }
}
