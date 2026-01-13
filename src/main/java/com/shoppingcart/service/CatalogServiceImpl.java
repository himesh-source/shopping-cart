package com.shoppingcart.service;

import com.shoppingcart.domain.CatalogProduct;
import com.shoppingcart.repository.CatalogRepository;

import java.math.BigDecimal;
import java.util.Objects;

public class CatalogServiceImpl implements CatalogService{
    private final PriceService priceService;
    private final CatalogRepository repository;

    public CatalogServiceImpl(PriceService priceService, CatalogRepository repository) {
        this.priceService = Objects.requireNonNull(priceService, "priceService must not be null");

        this.repository = repository;
    }


    @Override
    public CatalogProduct findByName(String productName) {
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("productName must not be null or blank");
        }

        BigDecimal price = priceService.getPrice(productName);
        if (price == null) {
            throw new IllegalStateException("Price service returned null for " + productName);
        }

        // Return immutable CatalogProduct
        return new CatalogProduct(productName, price);
    }

    // New method — uses in-memory repository
    public CatalogProduct findByNameInRepo(String productName) {
        return repository.findByName(productName);
    }

    @Override
    public void save(CatalogProduct product) {
        repository.save(product);
    }

    @Override
    public void changePrice(String productName, BigDecimal newPrice) {
        repository.changePrice(productName, newPrice);
    }


}
