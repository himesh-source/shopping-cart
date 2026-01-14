package com.shoppingcart.receipt;

import com.shoppingcart.dto.CartSummaryDto;

public interface ReceiptPrinter {
    String print(CartSummaryDto summary);
}
