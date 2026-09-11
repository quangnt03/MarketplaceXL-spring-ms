package com.example.marketplace.shared.exception;

public enum ProductReadinessFailure {
    MISSING_NAME,
    MISSING_DESCRIPTION,
    MISSING_CATEGORY,
    NO_ACTIVE_VARIANT,
    INVALID_SKU,
    DUPLICATE_SKU,
    INVALID_PRICE
}
