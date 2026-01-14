package com.shoppingcart.repository;

import com.shoppingcart.domain.CatalogProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryCatalogRepositoryTest {
    private InMemoryCatalogRepository repository;
    @BeforeEach
    void setUp() {
        repository = new InMemoryCatalogRepository();
    }

    @Test
    void findByName() {
        CatalogProduct product = new CatalogProduct("cornflakes", new BigDecimal("2.52"));

        repository.save(product);

        CatalogProduct found = repository.findByName("cornflakes");

        assertNotNull(found);
        assertEquals("cornflakes", found.getName());
        assertEquals(new BigDecimal("2.52"), found.getPrice());
    }

    @Test
    void save() {
        repository.save(new CatalogProduct("milk", new BigDecimal("10.00")));
        repository.save(new CatalogProduct("milk", new BigDecimal("12.00")));

        CatalogProduct found = repository.findByName("milk");

        assertEquals(new BigDecimal("12.00"), found.getPrice());
    }

    @Test
    void changePrice() {
        repository.save(new CatalogProduct("bread", new BigDecimal("8.00")));

        repository.changePrice("bread", new BigDecimal("9.50"));

        CatalogProduct found = repository.findByName("bread");

        assertEquals(new BigDecimal("9.50"), found.getPrice());
    }
}