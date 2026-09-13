package com.example.marketplace.shared.exception;

import java.util.Objects;

@SuppressWarnings("PMD.DataClass")
public class OwnershipMismatchException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String childType;
    private final String childId;
    private final String parentType;
    private final String expectedParentId;
    private final String actualParentId;

    public OwnershipMismatchException(
            String childType,
            Object childId,
            String parentType,
            Object expectedParentId,
            Object actualParentId) {
        super(buildMessage(childType, childId, parentType, expectedParentId, actualParentId));
        this.childType = requireText(childType, "childType");
        this.childId = Objects.requireNonNull(childId, "childId must not be null").toString();
        this.parentType = requireText(parentType, "parentType");
        this.expectedParentId =
                Objects.requireNonNull(expectedParentId, "expectedParentId must not be null").toString();
        this.actualParentId =
                Objects.requireNonNull(actualParentId, "actualParentId must not be null").toString();
    }

    public String getChildType() {
        return childType;
    }

    public String getChildId() {
        return childId;
    }

    public String getParentType() {
        return parentType;
    }

    public String getExpectedParentId() {
        return expectedParentId;
    }

    public String getActualParentId() {
        return actualParentId;
    }

    private static String buildMessage(
            String childType,
            Object childId,
            String parentType,
            Object expectedParentId,
            Object actualParentId) {
        return requireText(childType, "childType")
                + " "
                + Objects.requireNonNull(childId, "childId must not be null")
                + " belongs to "
                + requireText(parentType, "parentType")
                + " "
                + Objects.requireNonNull(actualParentId, "actualParentId must not be null")
                + ", expected "
                + Objects.requireNonNull(expectedParentId, "expectedParentId must not be null")
                + ".";
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }
}
