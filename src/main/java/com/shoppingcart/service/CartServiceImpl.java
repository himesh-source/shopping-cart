package com.shoppingcart.service;

import com.shoppingcart.constants.CartConstants;
import com.shoppingcart.domain.*;
import com.shoppingcart.dto.CartSummaryDto;
import com.shoppingcart.dto.DiscountDto;
import com.shoppingcart.dto.ProductRequestDto;
import com.shoppingcart.promotion.PromotionRulesEngineImpl;
import com.shoppingcart.receipt.Receipt;
import com.shoppingcart.receipt.ReceiptLine;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CartServiceImpl implements CartService {
    private final Cart cart = new Cart();
    private final CatalogService catalogService;
    private final PromotionRulesEngineImpl promotionEngine;

    public CartServiceImpl(CatalogService catalogService, PromotionRulesEngineImpl promotionEngine) {
        this.catalogService = catalogService;
        this.promotionEngine = promotionEngine;
    }

    @Override
    public void addProduct(ProductRequestDto request) {
        if (request.quantity() <= 0) throw new IllegalArgumentException("Quantity must be positive");

        // Fetch catalog product
        CatalogProduct catalogProduct = catalogService.findByName(request.productName());

        // Create immutable snapshot for cart
        CartProduct snapshot = new CartProduct(catalogProduct.getName(), catalogProduct.getPrice());

        // Thread-safe add
        cart.addItem(snapshot, request.quantity());
    }

    @Override
    public CartSummaryDto getSummary() {
        BigDecimal subtotal = round(cart.getItems().values().stream()
                .map(CartItem::total)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        BigDecimal discount = promotionEngine.calculateTotalDiscount(
                cart.getItems().values().stream().toList()
        );

        BigDecimal discountedSubtotal = round(subtotal.subtract(discount));

        BigDecimal tax = round(discountedSubtotal.multiply(CartConstants.TAX_RATE));
        BigDecimal total = round(discountedSubtotal.add(tax));

        var items = cart.getItems().values().stream()
                .collect(Collectors.toMap(
                        ci -> ci.getProduct().name(),
                        CartItem::getQuantity
                ));

        return new CartSummaryDto(items, discountedSubtotal, tax, total);
    }

    private BigDecimal round(BigDecimal value) {
        return value.setScale(CartConstants.MONEY_SCALE, CartConstants.MONEY_ROUNDING);
    }

    public Receipt getReceipt() {
        List<ReceiptLine> lines = new ArrayList<>();

        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem item : cart.getItems().values()) {

            BigDecimal original = item.total();
            Optional<DiscountDto> discount = promotionEngine.getDiscount(item);

            BigDecimal finalPrice = discount.get().finalPrice();
            BigDecimal discountAmount = original.subtract(finalPrice);

            subtotal = subtotal.add(finalPrice);

            lines.add(new ReceiptLine(
                    item.getProduct().name(),
                    item.getQuantity(),
                    original,
                    discountAmount,
                    finalPrice,
                    discount.get().description()
            ));
        }

        subtotal = round(subtotal);
        BigDecimal tax = round(subtotal.multiply(CartConstants.TAX_RATE));
        BigDecimal total = round(subtotal.add(tax));

        return new Receipt(lines, subtotal, tax, total);
    }
}
