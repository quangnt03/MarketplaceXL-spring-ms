package com.example.marketplace.shared.exception;

import java.util.Objects;

public class InvalidStatusOperationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String domainType;
    private final String domainId;
    private final String currentStatus;
    private final String attemptedOperation;

    public InvalidStatusOperationException(
            String domainType,
            Object domainId,
            Enum<?> currentStatus,
            String attemptedOperation) {
        super(buildMessage(domainType, domainId, currentStatus, attemptedOperation));
        this.domainType = requireText(domainType, "domainType");
        this.domainId = Objects.requireNonNull(domainId, "domainId must not be null").toString();
        this.currentStatus = Objects.requireNonNull(currentStatus, "currentStatus must not be null").name();
        this.attemptedOperation = requireText(attemptedOperation, "attemptedOperation");
    }

    public String getDomainType() {
        return domainType;
    }

    public String getDomainId() {
        return domainId;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public String getAttemptedOperation() {
        return attemptedOperation;
    }

    private static String buildMessage(
            String domainType,
            Object domainId,
            Enum<?> currentStatus,
            String attemptedOperation) {
        return "Cannot "
                + requireText(attemptedOperation, "attemptedOperation")
                + " for "
                + requireText(domainType, "domainType")
                + " "
                + Objects.requireNonNull(domainId, "domainId must not be null")
                + ": current status is "
                + Objects.requireNonNull(currentStatus, "currentStatus must not be null").name()
                + ".";
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }
}
