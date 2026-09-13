package com.example.marketplace.shared.state_management;

import java.util.Locale;
import java.util.Set;
import java.util.Objects;

import com.example.marketplace.shared.exception.IllegalLifecycleTransitionException;

public final class LifecycleStateMachine<E extends Enum<E>> implements IStateMachine<E> {
    private final String domainType;
    private final Object domainId;
    private E currentState;

    public LifecycleStateMachine(String domainType, Object domainId, E initialState) {
        this.domainType = requireText(domainType, "domainType");
        this.domainId = Objects.requireNonNull(domainId, "domainId must not be null");
        this.currentState = Objects.requireNonNull(initialState, "initialState must not be null");
    }

    @Override
    public E getState() {
        return currentState;
    }

    @Override
    public E transitionState(E targetState, Set<E> allowedCurrentState) {
        if (!allowedCurrentState.contains(this.currentState)) {
            throw new IllegalLifecycleTransitionException(
                this.domainType,
                this.domainId,
                this.currentState,
                targetState.toString().toUpperCase(Locale.ROOT)
            );
        }
        this.currentState = targetState;
        return this.currentState;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }
}

