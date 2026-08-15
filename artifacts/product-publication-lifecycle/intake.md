# Feature Intake: Product Publication Lifecycle Domain Slice

## Identity

- **Feature name:** Product Publication Lifecycle Domain Slice
- **Feature slug:** `product-publication-lifecycle`
- **Branch checked out:** `feature/product_lifecycle`
- **Requested by:** Marketplace product owner
- **Target milestone:** Milestone 3: Product and Storefront

## Problem and Outcome

- **Who has the problem?** MERCHANT
- **What problem should be solved?** A merchant needs a consistent domain flow for creating a product, drafting its first version, defining sellable variants and inventory, and publishing the complete version.
- **What outcome should the feature create?** One complete product version can move from `DRAFT` through `IN_REVIEW` to `PUBLISHED`, after which the product points to it as the current published version.
- **Why is it part of the Marketplace MVP?** Product creation and publication are required by Milestone 3 and are prerequisites for storefront discovery and purchase flows.

## Scope

- **In scope:** Pure Java domain entities and value objects for Product, ProductVersion, ProductVariant, ProductVariantVersion, Inventory, Money, publication readiness validation, lifecycle transitions, and unit tests.
- **Out of scope:** Spring, HTTP APIs, authentication, authorization, JPA, Flyway, Oracle, Redis, RabbitMQ, frontend behavior, file upload, search, checkout, multiple published versions, superseding versions, unpublishing, rejection, archival, and inventory reservations.
- **Affected areas:** Backend domain model and isolated domain unit tests.

## Success Criteria

1. Given a store identifier, when the merchant creates a product identity, then the product belongs to that store and has no publication lifecycle state.
2. Given a product, when its first version is created, then the version belongs to the product and starts in `DRAFT`.
3. Given a product version, when variants are created, then stable ProductVariant identity is separate from versioned SKU, name, and price data in ProductVariantVersion.
4. Given a product variant, when inventory is initialized, then exactly one inventory record tracks its non-negative available quantity without changing product lifecycle state.
5. Given a complete draft, when it is submitted, then its ProductVersion and included ProductVariantVersions move from `DRAFT` to `IN_REVIEW`.
6. Given an in-review version, when it is published, then it and its included variant versions move to `PUBLISHED`, and Product.currentPublishedVersionId points to it.
7. Given an incomplete version or an illegal transition, when submission or publication is attempted, then a domain exception is thrown and no partial lifecycle change occurs.
8. All lifecycle behavior is proven by domain unit tests that run without Spring or infrastructure.

## Main Flow

1. Merchant creates Product P1 for Store S1.
2. Merchant creates ProductVersion V1 for P1 in `DRAFT`.
3. Merchant edits V1 name, description, category, media references, and other versioned attributes.
4. Merchant creates stable ProductVariant identities A and B for P1.
5. Merchant creates ProductVariantVersion entries for V1 with SKUs `BLACK-S` and `BLACK-M` and Money prices 100 and 110.
6. Merchant initializes one Inventory record for each stable variant with available quantities 20 and 15.
7. Merchant submits V1, and readiness validation succeeds before V1 and its variant versions move to `IN_REVIEW`.
8. The version is published, its variant versions become `PUBLISHED`, and P1 records V1 as its current published version.
9. Storefront work in a later slice may resolve P1 to V1 and its published, available variants.

## Extreme and Failure Cases

| Area | What can happen? | Expected behavior |
|---|---|---|
| Minimum/maximum/empty input | Name, description, or category is absent; no active variant version exists | Submission throws a readiness domain exception and all states remain unchanged |
| Invalid or stale input | SKU is blank, duplicated within the version, or price is null, zero, negative, or lacks currency | Submission throws a readiness domain exception |
| Unauthenticated/wrong role | Domain-only slice has no authentication boundary | N/A; authorization belongs to the later application/API slice |
| Wrong user or tenant | Product, version, variant, or inventory identifiers belong to different aggregate ownership chains | Domain construction or association rejects the mismatch |
| Duplicate/retried request | Submit or publish is called after the state already changed | The repeated call is an illegal transition and throws a domain exception |
| Concurrent requests | Two callers attempt lifecycle changes simultaneously | N/A for the infrastructure-free slice; persistence locking is deferred |
| Dependency timeout/failure | No external dependency is invoked | N/A |
| Partial transaction failure | A child transition fails while changing the aggregate | Preconditions are checked for the full aggregate before mutation, so no partial state change occurs |
| Existing/legacy data | No persisted data is read or migrated | N/A |

## Product and UX Decisions

- **Required UI states:** N/A for this domain-only slice.
- **User-visible messages:** N/A; domain exceptions expose stable reason codes suitable for later API translation.
- **Manual approval or subjective behavior:** Publication is represented as an explicit domain operation after submission; actor workflow and RBAC are deferred.

## Constraints

- **Security/privacy:** Ownership identifiers must remain consistent across Product, ProductVersion, ProductVariant, ProductVariantVersion, and Inventory.
- **Performance/volume:** Readiness validation must be linear in the number of variant versions.
- **Compatibility:** Domain tests must use plain Java and JUnit without starting a Spring context.
- **Rollout or migration:** N/A for this domain-only slice.

## Open Questions

1. Should a later slice allow withdrawal, rejection, revision, superseding, and archival transitions already documented in `docs/md/05_transactions_and_state.md`?
2. Should inventory remain attached to stable ProductVariant identity across future ProductVersions, or should a later model support version-specific inventory policies?

## User Approval

- [x] I confirm the goal and scope.
- [x] I confirm the success criteria.
- [x] I confirm the main flow and critical extreme cases.
- [x] The agent may create the detailed feature specification.
