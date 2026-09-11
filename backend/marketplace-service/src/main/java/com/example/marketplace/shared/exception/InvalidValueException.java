package com.example.marketplace.inventory.exception;

import java.util.Objects;

public class InvalidQuantityException extends IllegalArgumentException{
    private final String domainType;
    private final String domainId;

    public InvalidQuantityException(String message, String domainType, String domainId) {
        super(buildMessage(domainType, domainId));
        this.domainId = Objects.requireNonNull(domainId, "domainId must not be null");
        this.domainType = Objects.requireNonNull(domainType, "domainType must not be null");

    }

    public String getDomainType() {
        return domainType;
    }

    public String getDomainId() {
        return domainId;
    }

    private static String buildMessage(String domainType, Object domainId) {
        return "Invalid argument for "
                + requireText(domainType, "domainType")
                + " "
                + Objects.requireNonNull(domainId, "domainId must not be null")
                + ": availableQuantity must be greater than 0";
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }
}
