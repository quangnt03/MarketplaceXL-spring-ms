package com.example.marketplace.shared.exception;

import java.io.Serial;
import java.util.Locale;
import java.util.Objects;

@SuppressWarnings("PMD.DataClass")
public class IllegalLifecycleTransitionException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String entityType;
    private final String entityId;
    private final String sourceState;
    private final String attemptedAction;

    public IllegalLifecycleTransitionException(
            String entityType,
            Object entityId,
            Enum<?> sourceState,
            String attemptedAction) {
        super(buildMessage(entityType, entityId, sourceState, attemptedAction));
        this.entityType = requireText(entityType, "entityType");
        this.entityId = Objects.requireNonNull(entityId, "entityId must not be null").toString();
        this.sourceState = Objects.requireNonNull(sourceState, "sourceState must not be null").name();
        this.attemptedAction = requireText(attemptedAction, "attemptedAction");
    }

    public String getEntityType() {
        return entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getSourceState() {
        return sourceState;
    }

    public String getAttemptedAction() {
        return attemptedAction;
    }

    private static String buildMessage(
            String entityType,
            Object entityId,
            Enum<?> sourceState,
            String attemptedAction) {
        return "Cannot switch state of "
                + requireText(entityType, "entityType")
                + " "
                + Objects.requireNonNull(entityId, "entityId must not be null")
                + " to "
                + requireText(attemptedAction, "attemptedAction").toUpperCase(Locale.ROOT)
                + " while it is "
                + Objects.requireNonNull(sourceState, "sourceState must not be null").name()
                + ".";
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }
}
