package com.example.marketplace.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.marketplace.shared.exception.InvalidStateTransitionException;
import com.example.marketplace.tenant.ETenantStatus;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class InvalidStateTransitionExceptionTest {

    private static final UUID TENANT_ID =
            UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Test
    void capturesUniversalTransitionContextForDomainErrorHandling() {
        InvalidStateTransitionException exception = new InvalidStateTransitionException(
                "Tenant",
                TENANT_ID,
                ETenantStatus.CLOSED,
                "reactivate");

        assertThat(exception)
                .isInstanceOf(RuntimeException.class)
                .hasMessage(
                        "Invalid state transition for Tenant "
                                + TENANT_ID
                                + ": cannot reactivate from CLOSED.");
        assertThat(exception.getDomainType()).isEqualTo("Tenant");
        assertThat(exception.getDomainId()).isEqualTo(TENANT_ID.toString());
        assertThat(exception.getCurrentState()).isEqualTo("CLOSED");
        assertThat(exception.getAttemptedTransition()).isEqualTo("reactivate");
    }

    @Test
    void supportsUseCaseSpecificExceptionSubclasses() {
        InvalidStateTransitionException exception =
                new TenantReactivationNotAllowedException(TENANT_ID, ETenantStatus.SUSPENDED);

        assertThat(exception)
                .isExactlyInstanceOf(TenantReactivationNotAllowedException.class)
                .hasMessageContaining("Tenant")
                .hasMessageContaining("SUSPENDED")
                .hasMessageContaining("reactivate");
        assertThat(exception.getDomainId()).isEqualTo(TENANT_ID.toString());
    }

    private static final class TenantReactivationNotAllowedException
            extends InvalidStateTransitionException {

        private TenantReactivationNotAllowedException(UUID tenantId, ETenantStatus currentState) {
            super("Tenant", tenantId, currentState, "reactivate");
        }
    }
}
