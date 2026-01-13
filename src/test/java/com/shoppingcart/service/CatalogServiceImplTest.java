package com.shoppingcart.service;

import com.shoppingcart.domain.CatalogProduct;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.shoppingcart.constants.CartConstants.MONEY_SCALE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CatalogServiceImplTest {

    @Mock
    private PriceService priceService;

    @InjectMocks
    private CatalogServiceImpl catalogService;

    @Test
    void findByNameReturnsCorrectProduct() {
        when(priceService.getPrice("cornflakes")).thenReturn(new BigDecimal("2.52"));

        CatalogProduct product = catalogService.findByName("cornflakes");

        assertEquals("cornflakes", product.getName());
        assertEquals(new BigDecimal("2.52").setScale(MONEY_SCALE), product.getPrice().setScale(MONEY_SCALE));
        verify(priceService).getPrice("cornflakes");
    }

    @Test
    void findByNameInvalidInputThrows() {
        assertThrows(IllegalArgumentException.class, () -> catalogService.findByName(null));
        assertThrows(IllegalArgumentException.class, () -> catalogService.findByName(""));
        assertThrows(IllegalArgumentException.class, () -> catalogService.findByName("  "));
    }

    @Test
    void threadSafetyTest() throws InterruptedException {
        when(priceService.getPrice("cornflakes")).thenReturn(new BigDecimal("2.52"));

        int threads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    CatalogProduct product = catalogService.findByName("cornflakes");
                    assertEquals("cornflakes", product.getName());
                    assertEquals(new BigDecimal("2.52").setScale(MONEY_SCALE), product.getPrice().setScale(MONEY_SCALE));
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();
    }
}
