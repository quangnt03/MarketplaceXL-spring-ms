package com.example.marketplace.tenant;

import com.example.marketplace.shared.exception.IllegalLifecycleTransitionException;
import com.example.marketplace.shared.state_management.LifecycleStateMachine;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class Tenant {

    private UUID id;
    private String tenantName;
    private LifecycleStateMachine<ETenantStatus> status;

    public static Tenant create(UUID id, String tenantName) {
        return new Tenant(id, tenantName);
    }

    private Tenant(UUID id, String tenantName) {
        this.setTenantId(id);
        this.setTenantName(tenantName);
        this.status = new LifecycleStateMachine<ETenantStatus>(
            "tenant", id, ETenantStatus.ACTIVE
        );
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public UUID getTenantId() {
        return id;
    }

    public void setTenantId(UUID id) {
        this.id = Objects.requireNonNull(id, "id must not be null");
    }

    public ETenantStatus getStatus() {
        return status.getState();
    }

    public ETenantStatus deactivate() {
        return status.transitionState(
            ETenantStatus.INACTIVE,
            Set.of(ETenantStatus.ACTIVE));
    }

    public ETenantStatus reactivate() {
        return status.transitionState(
                ETenantStatus.ACTIVE,
                Set.of(ETenantStatus.INACTIVE));
    }

    public ETenantStatus suspend() {
        return status.transitionState(
                ETenantStatus.SUSPENDED,
                Set.of(ETenantStatus.ACTIVE, ETenantStatus.INACTIVE));
    }

    public ETenantStatus reinstateAndActivate() {
        return status.transitionState(
                ETenantStatus.ACTIVE,
                Set.of(ETenantStatus.SUSPENDED));
    }

    public ETenantStatus reinstateWithoutActivation() {
        return status.transitionState(
                ETenantStatus.INACTIVE,
                Set.of(ETenantStatus.SUSPENDED));
    }

    public ETenantStatus close() {
        return status.transitionState(
                ETenantStatus.CLOSED,
                Set.of(ETenantStatus.ACTIVE,ETenantStatus.INACTIVE, ETenantStatus.SUSPENDED));
    }
}
