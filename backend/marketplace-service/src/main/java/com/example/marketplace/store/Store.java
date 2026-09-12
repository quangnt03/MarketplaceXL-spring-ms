package com.example.marketplace.store;

import com.example.marketplace.shared.exception.IllegalLifecycleTransitionException;

import java.util.Set;
import java.util.UUID;

public final class Store {
    private UUID id;
    private String storeName;
    private EStoreStatus storeStatus;

    public Store(UUID id, String storeName) {
        this.setId(id);
        this.setStoreName(storeName);
        this.storeStatus = EStoreStatus.DRAFT;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public EStoreStatus getStoreStatus() {
        return storeStatus;
    }

    private void setStoreStatus(EStoreStatus status) {
        this.storeStatus = status;
    }

    private EStoreStatus transitionState(EStoreStatus targetState, Set<EStoreStatus> allowedCurrentState) {
        EStoreStatus currentState = this.getStoreStatus();
        if (currentState == targetState) {
            return currentState;
        }
        else if (allowedCurrentState.contains(currentState)) {
            this.setStoreStatus(targetState);
            return this.getStoreStatus();
        }
        else {
            throw new IllegalLifecycleTransitionException(
                "store",
                this.getId(),
                this.getStoreStatus(),
                targetState.toString()
            );
        }
    }

    public EStoreStatus submitForReview() {
        return this.transitionState(
            EStoreStatus.PENDING_REVIEW,
            Set.of(EStoreStatus.DRAFT)
        );
    }

    public EStoreStatus approve() {
        return this.transitionState(
            EStoreStatus.ACTIVE,
            Set.of(EStoreStatus.PENDING_REVIEW)
        );
    }

    public EStoreStatus reject() {
        return this.transitionState(
            EStoreStatus.DRAFT,
            Set.of(EStoreStatus.PENDING_REVIEW)
        );
    }

    public EStoreStatus deactivate() {
        return this.transitionState(
            EStoreStatus.INACTIVE,
            Set.of(EStoreStatus.ACTIVE)
        );
    }

    public EStoreStatus reactivate() {
        return this.transitionState(
            EStoreStatus.ACTIVE,
            Set.of(EStoreStatus.INACTIVE)
        );
    }
    public EStoreStatus suspend() {
        return this.transitionState(
            EStoreStatus.SUSPENDED,
            Set.of(EStoreStatus.ACTIVE, EStoreStatus.INACTIVE, EStoreStatus.PENDING_REVIEW)
        );
    }
    public EStoreStatus reinstateAndActivate() {
        return this.transitionState(
            EStoreStatus.ACTIVE,
            Set.of(EStoreStatus.SUSPENDED)
        );
    }

    public EStoreStatus reinstateWithoutActivation() {
        return this.transitionState(
            EStoreStatus.INACTIVE,
            Set.of(EStoreStatus.SUSPENDED)
        );
    }

    public EStoreStatus resumeReview() {
        return this.transitionState(
            EStoreStatus.PENDING_REVIEW,
            Set.of(EStoreStatus.SUSPENDED)
        );
    }

    public EStoreStatus close() {
        return this.transitionState(
            EStoreStatus.CLOSED,
            Set.of(
                EStoreStatus.DRAFT,
                EStoreStatus.INACTIVE,
                EStoreStatus.PENDING_REVIEW,
                EStoreStatus.SUSPENDED
            )
        );
    }
}
