package com.example.marketplace.shared.exception;

import java.io.Serial;
import java.util.Objects;

@SuppressWarnings("PMD.DataClass")
public class InvalidValueException extends IllegalArgumentException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String argument;
    private final String constraints;
    private final String domainType;
    private final String domainId;

    public InvalidValueException(String argument, String constraints, String domainType, String domainId) {
        super(buildMessage(argument, constraints, domainType, domainId));
        this.argument = Objects.requireNonNull(argument, "argument must not be null");
        this.constraints = Objects.requireNonNull(constraints, "constraints must not be null");
        this.domainId = Objects.requireNonNull(domainId, "domainId must not be null");
        this.domainType = Objects.requireNonNull(domainType, "domainType must not be null");
    }

    public String getArgument() {
        return argument;
    }

    public String getConstraints() {
        return constraints;
    }

    public String getDomainType() {
        return domainType;
    }

    public String getDomainId() {
        return domainId;
    }

    private static String buildMessage(String argument, String constraints, String domainType, Object domainId) {
        return "Invalid argument for "
                + requireText(domainType, "domainType")
                + " "
                + Objects.requireNonNull(domainId, "domainId must not be null")
                + ": " + argument
                + "must be "
                + constraints;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }
}
