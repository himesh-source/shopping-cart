package com.shoppingcart.repository;

import com.shoppingcart.domain.CatalogProduct;

public interface ProductRepository {
    CatalogProduct findByName(String name);
}
