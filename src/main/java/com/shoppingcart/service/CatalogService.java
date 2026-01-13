package com.shoppingcart.service;

import com.shoppingcart.domain.CatalogProduct;

import java.math.BigDecimal;

public interface CatalogService {
    /**
     * Finds a catalog product by its name.
     *
     * @param productName the product name, must not be null or blank
     * @return the catalog product if found, or null if not found
     * @throws IllegalArgumentException if productName is null or blank
     * @throws IllegalStateException for price is null or blank)
     */
    CatalogProduct findByName(String productName);

      void save(CatalogProduct product);
      void changePrice(String productName, BigDecimal newPrice);

}
