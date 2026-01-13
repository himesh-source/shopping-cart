package com.shoppingcart.service;

import com.shoppingcart.domain.CatalogProduct;
import com.shoppingcart.domain.Receipt;
import com.shoppingcart.domain.ReceiptLine;
import com.shoppingcart.dto.CartSummaryDto;
import com.shoppingcart.dto.ProductRequestDto;
import com.shoppingcart.factory.CartServiceFactory;
import com.shoppingcart.promotion.DiscountPercentageRule;
import com.shoppingcart.promotion.FreeItemPromotionalRule;
import com.shoppingcart.promotion.PromotionRulesEngineImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.shoppingcart.constants.CartConstants.MONEY_SCALE;
import static com.shoppingcart.constants.CartConstants.TAX_RATE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {
    private CartServiceImpl cartService;
    @Mock
    private  CatalogServiceImpl catalogService;

    @BeforeEach
    void setUp() {
        // Fake CatalogService for testing
        CatalogService catalogService = new CatalogService() {
            @Override
            public CatalogProduct findByName(String productName) {
                if ("cornflakes".equalsIgnoreCase(productName)) {
                    return new CatalogProduct("cornflakes", new BigDecimal("2.52"));
                } else if ("weetbix".equalsIgnoreCase(productName)) {
                    return new CatalogProduct("weetbix", new BigDecimal("9.98"));
                } else {
                    return new CatalogProduct(productName, new BigDecimal("10.00"));
                }
            }

            @Override
            public void save(CatalogProduct product) {
                // no-op for testing
            }

            @Override
            public void changePrice(String productName, BigDecimal newPrice) {
                // no-op for testing
            }
        };

        PromotionRulesEngineImpl engine = new PromotionRulesEngineImpl(List.of(
                new FreeItemPromotionalRule("weetbix", 2, 1),      // Buy 2 Get 1 Free
                new DiscountPercentageRule("cornflakes", new BigDecimal("10")) // 10% off
        ));

        cartService = new CartServiceImpl(catalogService, engine);
        // Fake CatalogService for testing
       /* CatalogService catalogService = productName -> {
            if ("cornflakes".equalsIgnoreCase(productName)) {
                return new CatalogProduct("cornflakes", new BigDecimal("2.52"));
            } else if ("weetbix".equalsIgnoreCase(productName)) {
                return new CatalogProduct("weetbix", new BigDecimal("9.98"));
            } else {
                return new CatalogProduct(productName, new BigDecimal("10.00"));
            }
        };

        PromotionRulesEngineImpl engine = new PromotionRulesEngineImpl(List.of(
                new FreeItemPromotionalRule("weetbix", 2, 1),      // Buy 2 Get 1 Free
                new DiscountPercentageRule("cornflakes", new BigDecimal("10")) // 10% off
        ));

        cartService = new CartServiceImpl(catalogService, engine);*/
    }

    @Test
    void testAddProductsAndSummary() {
        cartService.addProduct(new ProductRequestDto("cornflakes", 2));
        cartService.addProduct(new ProductRequestDto("weetbix", 2));

        CartSummaryDto summary = cartService.getSummary();

        // cornflakes: 2 x 2.52 = 5.04 - 10% = 4.54
        // weetbix: 3 x 9.98 = 29.94 - Buy2Get1 = 19.96
        // subtotal = 24.50
        assertEquals(new BigDecimal("24.50"), summary.subtotal());

        BigDecimal expectedTax = new BigDecimal("3.06").setScale(2, summary.subtotal().ROUND_HALF_UP); // TAX_RATE = 12.5%
        // your CartConstants.TAX_RATE = 0.125
        assertEquals(new BigDecimal("3.06"), summary.tax()); // rounding
        assertEquals(new BigDecimal("27.56"), summary.total());
    }

    @Test
    void testNoDuplicateDiscounts() {
        cartService.addProduct(new ProductRequestDto("cornflakes", 5));

        // Only PercentageDiscountRule applies, BuyXGetYFree does not apply
        CartSummaryDto summary = cartService.getSummary();

        // 5 x 2.52 = 12.60
        // 10% off = 1.26
        // subtotal = 11.34
        assertEquals(new BigDecimal("11.34"), summary.subtotal());
    }

    @Test
    void testBuyXGetYFreePartial() {
        cartService.addProduct(new ProductRequestDto("weetbix", 3));

        // Buy 2 Get 1 Free => 3 items: 1 free
        // 3 x 9.98, 1 free = 9.98discount, subtotal = 19.96
        CartSummaryDto summary = cartService.getSummary();
        assertEquals(new BigDecimal("19.96"), summary.subtotal());
    }

    @Test
    void testCartWithoutPromotions() {
        cartService.addProduct(new ProductRequestDto("rice", 2));

        // Price 10 each from fake catalog
        CartSummaryDto summary = cartService.getSummary();

        assertEquals(new BigDecimal("20.00"), summary.subtotal());
    }

    @Test
    void testAddProductInvalidQuantity() {
        assertThrows(IllegalArgumentException.class,
                () -> cartService.addProduct(new ProductRequestDto("cornflakes", 0)));
        assertThrows(IllegalArgumentException.class,
                () -> cartService.addProduct(new ProductRequestDto("cornflakes", -1)));
    }

    @Test
    void testGetSummaryEmptyCart() {
        CartSummaryDto summary = cartService.getSummary();
        assertEquals(BigDecimal.ZERO.setScale(MONEY_SCALE), summary.subtotal());
        assertEquals(BigDecimal.ZERO.setScale(MONEY_SCALE), summary.tax());
        assertEquals(BigDecimal.ZERO.setScale(MONEY_SCALE), summary.total());
    }

    @Test
    void testConcurrentAdds() throws InterruptedException {
        // Arrange: stub catalogService
        when(catalogService.findByName("cornflakes"))
                .thenReturn(new CatalogProduct("cornflakes", new BigDecimal("2.52")));

        // Use an empty promotion engine for this test
        PromotionRulesEngineImpl noPromotions = new PromotionRulesEngineImpl(List.of());
        CartServiceImpl concurrentCartService = new CartServiceImpl(catalogService, noPromotions);

        int threads = 10;
        int quantityPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < quantityPerThread; j++) {
                    concurrentCartService.addProduct(new ProductRequestDto("cornflakes", 1));
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        CartSummaryDto summary = concurrentCartService.getSummary();
        int expectedQuantity = threads * quantityPerThread;

        BigDecimal expectedSubtotal = new BigDecimal("2.52")
                .multiply(BigDecimal.valueOf(expectedQuantity))
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);

        BigDecimal expectedTax = expectedSubtotal
                .multiply(TAX_RATE)
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);

        BigDecimal expectedTotal = expectedSubtotal.add(expectedTax)
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);

        assertEquals(expectedQuantity, summary.items().get("cornflakes"));
        assertEquals(expectedSubtotal, summary.subtotal());
        assertEquals(expectedTax, summary.tax());
        assertEquals(expectedTotal, summary.total());
    }

    @Test
    void receipt_shouldShowDiscountLine() {
        CartService service = CartServiceFactory.createDefault();

        service.addProduct(new ProductRequestDto("cornflakes", 2));

        Receipt receipt = service.getReceipt();

        ReceiptLine line = receipt.getLines().get(0);

        assertEquals("cornflakes", line.getProduct());
        assertEquals(new BigDecimal("0.504"), line.getDiscount());
    }

}