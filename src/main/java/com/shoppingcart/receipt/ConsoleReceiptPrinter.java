package com.shoppingcart.receipt;

import com.shoppingcart.dto.CartSummaryDto;

public class ConsoleReceiptPrinter implements ReceiptPrinter {
    public String print(CartSummaryDto summary) {
        StringBuilder sb = new StringBuilder();

        sb.append("==== RECEIPT ====\n");

        summary.items().forEach((name, qty) ->
                sb.append(name).append(" x").append(qty).append("\n")
        );

        sb.append("----------------\n");
        sb.append("Subtotal: ").append(summary.subtotal()).append("\n");
        sb.append("Tax: ").append(summary.tax()).append("\n");
        sb.append("Total: ").append(summary.total()).append("\n");
        sb.append("================\n");

        return sb.toString();
    }
}
