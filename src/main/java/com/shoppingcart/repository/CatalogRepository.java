package com.shoppingcart.repository;

import com.shoppingcart.domain.CatalogProduct;

import java.math.BigDecimal;

public interface CatalogRepository {
    CatalogProduct findByName(String name);
    void save(CatalogProduct product);
    void changePrice(String productName, BigDecimal newPrice);
}
