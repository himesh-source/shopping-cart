package com.shoppingcart.repository;

import com.shoppingcart.domain.Cart;

public interface CartRepository {
    void save(Cart cart);
    Cart load(String cartId);
}
