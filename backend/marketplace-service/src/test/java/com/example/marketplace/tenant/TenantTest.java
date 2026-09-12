package com.example.marketplace.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.marketplace.shared.exception.IllegalLifecycleTransitionException;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TenantTest {

    private static final UUID TENANT_ID =
            UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Test
    void newTenantStartsActiveAndRetainsItsIdentity() {
        Tenant tenant = Tenant.create(TENANT_ID, "Acme");

        assertThat(tenant.getTenantId()).isEqualTo(TENANT_ID);
        assertThat(tenant.getTenantName()).isEqualTo("Acme");
        assertThat(tenant.getStatus()).isEqualTo(ETenantStatus.ACTIVE);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("validTransitions")
    void validStateTransitionChangesTenantStatus(
            String description,
            ETenantStatus initialStatus,
            Function<Tenant, ETenantStatus> transition,
            ETenantStatus expectedStatus) {
        Tenant tenant = tenantIn(initialStatus);

        ETenantStatus result = transition.apply(tenant);

        assertThat(result).isEqualTo(expectedStatus);
        assertThat(tenant.getStatus()).isEqualTo(expectedStatus);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidTransitions")
    void invalidStateTransitionThrowsUniversalExceptionWithoutMutation(
            String description,
            ETenantStatus initialStatus,
            String attemptedTransition,
            Function<Tenant, ETenantStatus> transition) {
        Tenant tenant = tenantIn(initialStatus);

        assertThatThrownBy(() -> transition.apply(tenant))
                .isInstanceOf(IllegalLifecycleTransitionException.class)
                .hasMessageContaining("tenant")
                .hasMessageContaining(TENANT_ID.toString())
                .hasMessageContaining(initialStatus.name())
                .hasMessageContaining(attemptedTransition)
                .satisfies(error -> {
                    IllegalLifecycleTransitionException transitionError =
                            (IllegalLifecycleTransitionException) error;
                    assertThat(transitionError.getEntityType()).isEqualTo("Tenant");
                    assertThat(transitionError.getEntityId()).isEqualTo(TENANT_ID.toString());
                    assertThat(transitionError.getSourceState()).isEqualTo(initialStatus.name());
                    assertThat(transitionError.getAttemptedAction())
                            .isEqualTo(attemptedTransition);
                });
        assertThat(tenant.getStatus()).isEqualTo(initialStatus);
    }

    private static Stream<Arguments> validTransitions() {
        return Stream.of(
                transition("ACTIVE can be deactivated", ETenantStatus.ACTIVE, Tenant::deactivate, ETenantStatus.INACTIVE),
                transition("INACTIVE can be reactivated", ETenantStatus.INACTIVE, Tenant::reactivate, ETenantStatus.ACTIVE),
                transition("ACTIVE can be suspended", ETenantStatus.ACTIVE, Tenant::suspend, ETenantStatus.SUSPENDED),
                transition("INACTIVE can be suspended", ETenantStatus.INACTIVE, Tenant::suspend, ETenantStatus.SUSPENDED),
                transition(
                        "SUSPENDED can be reinstated as active",
                        ETenantStatus.SUSPENDED,
                        Tenant::reinstateAndActivate,
                        ETenantStatus.ACTIVE),
                transition(
                        "SUSPENDED can be reinstated as inactive",
                        ETenantStatus.SUSPENDED,
                        Tenant::reinstateWithoutActivation,
                        ETenantStatus.INACTIVE),
                transition("ACTIVE can be closed", ETenantStatus.ACTIVE, Tenant::close, ETenantStatus.CLOSED),
                transition("INACTIVE can be closed", ETenantStatus.INACTIVE, Tenant::close, ETenantStatus.CLOSED),
                transition("SUSPENDED can be closed", ETenantStatus.SUSPENDED, Tenant::close, ETenantStatus.CLOSED));
    }

    private static Stream<Arguments> invalidTransitions() {
        return Stream.of(
                invalid("INACTIVE cannot be deactivated", ETenantStatus.INACTIVE, "deactivate", Tenant::deactivate),
                invalid("SUSPENDED cannot be deactivated", ETenantStatus.SUSPENDED, "deactivate", Tenant::deactivate),
                invalid("CLOSED cannot be deactivated", ETenantStatus.CLOSED, "deactivate", Tenant::deactivate),
                invalid("ACTIVE cannot be reactivated", ETenantStatus.ACTIVE, "reactivate", Tenant::reactivate),
                invalid("SUSPENDED cannot be reactivated", ETenantStatus.SUSPENDED, "reactivate", Tenant::reactivate),
                invalid("CLOSED cannot be reactivated", ETenantStatus.CLOSED, "reactivate", Tenant::reactivate),
                invalid("SUSPENDED cannot be suspended again", ETenantStatus.SUSPENDED, "suspend", Tenant::suspend),
                invalid("CLOSED cannot be suspended", ETenantStatus.CLOSED, "suspend", Tenant::suspend),
                invalid(
                        "ACTIVE cannot use suspended-to-active reinstatement",
                        ETenantStatus.ACTIVE,
                        "reinstateAndActivate",
                        Tenant::reinstateAndActivate),
                invalid(
                        "INACTIVE cannot use suspended-to-active reinstatement",
                        ETenantStatus.INACTIVE,
                        "reinstateAndActivate",
                        Tenant::reinstateAndActivate),
                invalid(
                        "CLOSED cannot use suspended-to-active reinstatement",
                        ETenantStatus.CLOSED,
                        "reinstateAndActivate",
                        Tenant::reinstateAndActivate),
                invalid(
                        "ACTIVE cannot use suspended-to-inactive reinstatement",
                        ETenantStatus.ACTIVE,
                        "reinstateWithoutActivation",
                        Tenant::reinstateWithoutActivation),
                invalid(
                        "INACTIVE cannot use suspended-to-inactive reinstatement",
                        ETenantStatus.INACTIVE,
                        "reinstateWithoutActivation",
                        Tenant::reinstateWithoutActivation),
                invalid(
                        "CLOSED cannot use suspended-to-inactive reinstatement",
                        ETenantStatus.CLOSED,
                        "reinstateWithoutActivation",
                        Tenant::reinstateWithoutActivation),
                invalid("CLOSED cannot be closed again", ETenantStatus.CLOSED, "close", Tenant::close));
    }

    private static Arguments transition(
            String description,
            ETenantStatus initialStatus,
            Function<Tenant, ETenantStatus> transition,
            ETenantStatus expectedStatus) {
        return Arguments.of(description, initialStatus, transition, expectedStatus);
    }

    private static Arguments invalid(
            String description,
            ETenantStatus initialStatus,
            String attemptedTransition,
            Function<Tenant, ETenantStatus> transition) {
        return Arguments.of(description, initialStatus, attemptedTransition, transition);
    }

    private static Tenant tenantIn(ETenantStatus status) {
        Tenant tenant = Tenant.create(TENANT_ID, "Acme");
        switch (status) {
            case ACTIVE -> {
                return tenant;
            }
            case INACTIVE -> tenant.deactivate();
            case SUSPENDED -> tenant.suspend();
            case CLOSED -> tenant.close();
        }
        return tenant;
    }
}
