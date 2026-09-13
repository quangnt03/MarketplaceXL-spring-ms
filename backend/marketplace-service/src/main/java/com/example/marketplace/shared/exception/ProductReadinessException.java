package com.example.marketplace.shared.exception;

import java.util.Objects;

public class ProductReadinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final ProductReadinessFailure failure;

    public ProductReadinessException(ProductReadinessFailure failure) {
        super(buildMessage(failure));
        this.failure = Objects.requireNonNull(failure, "failure must not be null");
    }

    public ProductReadinessFailure getFailure() {
        return failure;
    }

    private static String buildMessage(ProductReadinessFailure failure) {
        return "Product version is not ready for submission: "
                + Objects.requireNonNull(failure, "failure must not be null").name()
                + ".";
    }
}
