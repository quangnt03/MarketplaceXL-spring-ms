package com.example.marketplace.shared.state_management;

import java.util.List;

public interface IState<T> {
    T getState();
    T setState();
    T transitionState(T targetState, List<T> allowedCurrentState);
}
