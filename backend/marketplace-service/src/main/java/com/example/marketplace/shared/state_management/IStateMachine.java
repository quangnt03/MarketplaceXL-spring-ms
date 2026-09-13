package com.example.marketplace.shared.state_management;

import java.util.Set;

public interface IStateMachine<T> {
    T getState();

    T transitionState(T targetState, Set<T> allowedCurrentState);
}
