package com.shoppingcart.service;

import com.shoppingcart.constants.CartConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.shoppingcart.constants.CartConstants.MONEY_SCALE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceApiServiceTest {

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    @InjectMocks
    private PriceApiService priceApiService;

    @Test
    void testGetPriceParsesJsonCorrectly() throws Exception {
        when(httpResponse.body()).thenReturn("{\"price\":\"2.52\"}");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        BigDecimal price = priceApiService.getPrice("cornflakes");

        assertEquals(new BigDecimal("2.52").setScale(MONEY_SCALE), price.setScale(MONEY_SCALE));
    }

    @Test
    void testRequestUrlIsCorrect() throws Exception {
        when(httpResponse.body()).thenReturn("{\"price\":\"9.98\"}");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        priceApiService.getPrice("weetabix");

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(requestCaptor.capture(), any(HttpResponse.BodyHandler.class));

        HttpRequest request = requestCaptor.getValue();
        assertEquals("GET", request.method());
        assertEquals(CartConstants.PRICE_API_BASE_URL + "/weetabix.json", request.uri().toString());
    }

    @Test
    void testInvalidJsonThrowsRuntimeException() throws Exception {
        when(httpResponse.body()).thenReturn("invalid-json");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        assertThrows(RuntimeException.class, () -> priceApiService.getPrice("cornflakes"));
    }

    @Test
    void testThreadSafetyConcurrentCalls() throws Exception {
        when(httpResponse.body()).thenReturn("{\"price\":\"3.00\"}");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        int threads = 10;
        int callsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < callsPerThread; j++) {
                    BigDecimal price = priceApiService.getPrice("cornflakes");
                    assertEquals(new BigDecimal("3.00").setScale(MONEY_SCALE), price.setScale(MONEY_SCALE));
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();
    }
}