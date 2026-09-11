package com.example.marketplace.shared.exception;

import java.util.Objects;

public class DuplicateInventoryException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String productVariantId;

    public DuplicateInventoryException(Object productVariantId) {
        super(buildMessage(productVariantId));
        this.productVariantId =
                Objects.requireNonNull(productVariantId, "productVariantId must not be null").toString();
    }

    public String getProductVariantId() {
        return productVariantId;
    }

    private static String buildMessage(Object productVariantId) {
        return "Inventory already exists for product_variant "
                + Objects.requireNonNull(productVariantId, "productVariantId must not be null")
                + ".";
    }
}
