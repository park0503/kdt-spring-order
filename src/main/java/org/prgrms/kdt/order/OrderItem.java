package org.prgrms.kdt.order;

import java.util.UUID;

public class OrderItem {
    public final UUID productId;
    public final Long  productPrice;
    public final long quantity;

    public OrderItem(UUID productId, Long productPrice, long quantity) {
        this.productId = productId;
        this.productPrice = productPrice;
        this.quantity = quantity;
    }

    public UUID getProductId() {
        return productId;
    }

    public Long getProductPrice() {
        return productPrice;
    }

    public long getQuantity() {
        return quantity;
    }
}
