package com.shoppingcart.receipt;

import java.math.BigDecimal;
import java.util.List;

public class Receipt {
    private final List<ReceiptLine> lines;
    private final BigDecimal subtotal;
    private final BigDecimal tax;
    private final BigDecimal total;

    public Receipt(List<ReceiptLine> lines, BigDecimal subtotal, BigDecimal tax, BigDecimal total) {
        this.lines = lines;
        this.subtotal = subtotal;
        this.tax = tax;
        this.total = total;
    }

    public List<ReceiptLine> getLines() { return lines; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getTax() { return tax; }
    public BigDecimal getTotal() { return total; }
}
