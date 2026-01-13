package com.shoppingcart.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoppingcart.constants.CartConstants;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

public class PriceApiService implements PriceService {
    private final HttpClient httpClient;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public PriceApiService(HttpClient httpClient) {
        this.httpClient = Objects.requireNonNull(httpClient, "httpClient must not be null");
    }

    @Override
    public BigDecimal getPrice(String productName) {
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("productName must not be null or blank");
        }

        try {
            String url = CartConstants.PRICE_API_BASE_URL + "/" + productName + ".json";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode json = MAPPER.readTree(response.body());

            BigDecimal price = new BigDecimal(json.get("price").asText());
            return price.setScale(CartConstants.MONEY_SCALE, CartConstants.MONEY_ROUNDING);

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch price for " + productName, e);
        }
    }



}
