package com.shoppingcart.repository;

import com.shoppingcart.domain.CatalogProduct;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCatalogRepository implements CatalogRepository{
    private final Map<String, CatalogProduct> products = new ConcurrentHashMap<>();

    @Override
    public CatalogProduct findByName(String name) {
        CatalogProduct p = products.get(name);
        if (p == null) throw new IllegalArgumentException("Product not found: " + name);
        return p;
    }

    @Override
    public void save(CatalogProduct product) {
        products.put(product.getName(), product);
    }

    @Override
    public void changePrice(String productName, BigDecimal newPrice) {
        CatalogProduct existing = findByName(productName);
        CatalogProduct updated = new CatalogProduct(existing.getName(), newPrice);
        save(updated);
    }
}
