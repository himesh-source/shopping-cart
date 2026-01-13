package com.shoppingcart.factory;

import com.shoppingcart.promotion.DiscountPercentageRule;
import com.shoppingcart.promotion.FreeItemPromotionalRule;
import com.shoppingcart.promotion.PromotionRulesEngineImpl;
import com.shoppingcart.repository.CatalogRepository;
import com.shoppingcart.repository.InMemoryCatalogRepository;
import com.shoppingcart.service.*;

import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.util.List;

public final class CartServiceFactory {
    private CartServiceFactory() {}

    public static CartService createDefault() {
       // Create in-memory repository
        CatalogRepository repository = new InMemoryCatalogRepository();
        PriceService priceService = new PriceApiService(HttpClient.newHttpClient());
        CatalogService catalogService = new CatalogServiceImpl(priceService, repository);

        PromotionRulesEngineImpl promotionEngine = new PromotionRulesEngineImpl(List.of(
                new FreeItemPromotionalRule("weetbix", 1, 1),
                new DiscountPercentageRule("cornflakes", new BigDecimal("10"))
        ));

        return new CartServiceImpl(catalogService, promotionEngine);
    }
}
