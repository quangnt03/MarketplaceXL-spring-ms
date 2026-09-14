# Feature Intake: Product Lifecycle Application Integration

> Drafted by the agent from the Phase 2 planning sessions on 2026-09-13 and 2026-09-14.
> Every decision below was made by the user during those sessions or comes from the approved Phase 1 artifacts.
> Values marked "(assumption)" are agent proposals the user has not confirmed yet.

## Identity

- **Feature name:** Product Publication Lifecycle
- **Feature slug:** `product-publication-lifecycle`
- **Phase:** `application-integration` (Phase 2)
- **Branch checked out:** `feature/product_lifecycle`
- **Requested by:** Marketplace product owner
- **Target milestone:** Milestone 3: Product and Storefront
- **Builds on:** [core-domain specification](../core-domain/specification.md) (Phase 1 domain slice, referenced below as `P1-AC-xx`)

## Problem and Outcome

- **Who has the problem?** MERCHANT store members (create, edit, submit, list) and `STORE_ADMIN` store members (publish).
- **What problem should be solved?** The Phase 1 lifecycle exists only as in-memory Java objects.
  Nothing can be created, stored, listed, reviewed, or published through the running service, so no client or later module can use it.
- **What outcome should the feature create?** A merchant can create a product, its variants, inventory, and a draft version with offers through a versioned REST API, list variants and versions page by page, and submit a version for review; a store admin can publish it.
  Every command is persisted in Oracle atomically, authorized by platform role and store membership, documented in OpenAPI, logged in a structured format, and verified end to end against a real database and the packaged service image.
- **Why is it part of the Marketplace MVP?** Milestone 3 requires "Product create and update" and "Merchant can publish complete product", and storefront, cart, and checkout depend on persisted published versions.

## Scope

- **In scope:**
  - OpenAPI 3.1 contract for eleven product lifecycle operations under `/api/v1`.
  - Two cursor-paginated collection endpoints: variants of a product and versions of a product.
  - Request/response DTOs with structural validation.
  - Controllers, RFC 9457 error mapping, `ETag`/`If-Match`, and `X-Flow-ID` correlation.
  - Structured, extendable logging configured through Spring Boot (no custom logging abstraction).
  - Application use-case service with one transaction per command, authorization before locking, and a pessimistic product lock.
  - Separate JPA entities, Spring Data repositories, and a repository adapter.
  - Flyway migrations for lifecycle constraints, keyset pagination indexes, and the Spring Modulith event publication table.
  - Stubbed authentication behind a `CurrentActorResolver` interface, with real platform-role, store-membership, and store-role authorization.
  - Minimal domain changes required for persistence (rehydration) and defects found while mapping.
  - Amendment of `contracts/convention/api-conventions.md` (URL major versioning, three-level nesting, 422/428/503, pagination defaults) and alignment of `docs/md/07_testing_plan.md`.
  - Unit, migration, repository, service, web/contract, and E2E API tests on Oracle Testcontainers.
  - A containerized component test that runs the packaged service Docker image against an Oracle Testcontainer and drives it with a Java client, catching packaging and configuration defects the in-JVM tests cannot reach.
- **Out of scope:**
  - Real identity provider integration (Cognito or any JWT issuer).
  - Store, tenant, user, membership, role, and category management APIs (tests seed these rows).
  - Listing products of a store.
  - Publishing or consuming application events, Redis caching, RabbitMQ.
  - Second-version publication, superseding, withdrawal, rejection, and archival of versions.
  - Product slug and its per-tenant uniqueness (deferred to the storefront slice, recorded deviation).
  - Separate price history (`product_version_prices` stays untouched).
  - Storefront/public reads, file upload, image upload, search.
  - Frontend pages and Playwright tests.
  - CI workflow and `compose.yaml` changes (reported to the user, not implemented by the agent).
- **Affected areas:** backend domain (small), API, database, security, observability, contracts, conventions, tests.

## Success Criteria

1. Given an active store member whose principal has the platform MERCHANT role, when they create a product, variants, inventory, and a draft version with offers through the API, then every resource is persisted and returned with the documented status codes and bodies.
2. Given a complete draft version, when a store member submits it for review and a `STORE_ADMIN` member of the same store publishes it, then the version and offers are `PUBLISHED` and the product points to the version, each command committed in one transaction.
3. Given any Phase 1 domain rejection (readiness, illegal transition, invalid product status, ownership mismatch), when it happens through the API, then the client receives a stable problem type and no database row changes.
4. Given an unauthenticated caller, a principal without the platform MERCHANT role, a caller outside the owning store, or a member without `STORE_ADMIN` attempting to publish, when they call a product endpoint, then the request is rejected with 401, 403, 404, or 403 respectively, and no data from another store is revealed.
5. Given a product with more variants or versions than one page, when a member pages through the list endpoints, then every item appears exactly once in a deterministic order, even if rows are inserted between page requests.
6. Given two concurrent lifecycle commands on the same product, when both run, then the final state equals some serial execution of them.
7. Given a clean database or a database at V2, when Flyway migrates, then it reaches the latest version and Hibernate schema validation passes.
8. Every response of every operation conforms to `contracts/openapi/marketplace-api.yaml`, verified by automated contract tests, including against the packaged service image.

## Main Flow

1. Merchant M1, an active member of store S1, creates product P1 with a product code for S1.
2. M1 creates stable variants A and B for P1.
3. M1 lists P1's variants to obtain their ids.
4. M1 initializes inventory for A and B.
5. M1 creates draft version V1 for P1.
6. M1 replaces V1 content (name, description, category, media references with an explicit primary) and offers (SKU, display name, price per variant), sending the version's `ETag` in `If-Match`.
7. M1 submits V1 for review; readiness passes; V1 and its offers become `IN_REVIEW`.
8. A2, a `STORE_ADMIN` member of S1, publishes V1; V1 and offers become `PUBLISHED`; P1 points to V1.
9. M1 reads P1 and lists P1's versions and sees the published state.

## Extreme and Failure Cases

| Area | What can happen? | Expected behavior |
|---|---|---|
| Minimum/maximum/empty input | Missing required field, over-long text, too many offers or media references, more than one primary media reference, negative quantity, malformed JSON, unknown property, `limit` outside 1-100, unknown sort or filter value, invalid cursor | 400 `validation-failed` with per-field `errors`, no state change |
| Invalid or stale input | Draft incomplete at submission or publication (missing content, no active variant, no offers, blank or duplicate SKU, non-positive price); stale `If-Match`; `PUT` without `If-Match` | 422 `product-not-ready` with reason code; 412 `precondition-failed`; 428 `precondition-required`; no state change |
| Unauthenticated/wrong role | No credentials; principal without platform MERCHANT role; member without `STORE_ADMIN` publishes | 401 `unauthenticated`; 403 `forbidden`; 403 `forbidden` |
| Wrong user or tenant | Caller who is not an active member of S1 (including a `STORE_ADMIN` of S2) addresses S1 or any product, variant, or version of S1, or lists them | 404 `resource-not-found`, existence concealed, no data returned, no row lock taken |
| Duplicate/retried request | Duplicate product code or variant code; second version for a product; repeated submit or publish; repeated inventory PUT | 409 `duplicate-resource`; 409 `invalid-status-operation`; 409 `illegal-lifecycle-transition`; identical inventory PUT replays 200, differing body 409 |
| Concurrent requests | Publish races discontinue; two edits of the same draft; insertion between page requests | Commands serialize on the product row; loser observes committed state and fails with a domain problem, 409 `concurrent-modification`, or 412; pagination neither skips nor repeats items |
| Dependency timeout/failure | Oracle unreachable; row lock wait times out | Transaction rolls back and data stays consistent; 503 `service-unavailable` with `Retry-After`; lock timeout 409 `concurrent-modification`; readiness probe reports `db` down |
| Partial transaction failure | One offer fails publication validation after others passed | Whole command rolls back; no row changes |
| Existing/legacy data | Existing V2 database with product rows; global unique constraints replaced by scoped ones | Migration succeeds; existing rows remain valid (assumption: existing dev data satisfies new CHECK constraints) |

## Product and UX Decisions

- **Required UI states:** N/A, no frontend in this phase.
- **User-visible messages:** Problem `detail` is a fixed safe sentence per problem type; clients act on `type` and extension codes such as `reason`, never on `detail`.
- **Manual approval or subjective behavior:** Publication is a `STORE_ADMIN` decision (`IN_REVIEW -> PUBLISHED`); plain store members cannot publish.
  `STORE_ADMIN` is a store-scoped role (`roles.scope_type = 'STORE'`) held through `store_memberships`, typically by the store or tenant owner.
  It is distinct from the platform `ADMIN` role in `AGENTS.md`'s Security Rules and grants no access to other stores.

## Constraints

- **Security/privacy:** Authentication is stubbed (dev/test only, fail closed otherwise) behind a `CurrentActorResolver` interface, so a JWT resource server can be added as a second implementation without changing authorization.
  Platform roles come from the principal; store roles come only from `store_memberships`.
  Membership is checked before any store role and before any row lock, so non-members always see 404 and cannot cause lock contention.
  Tenant and caller identity never come from the request; responses never expose stack traces, SQL, exception class names, or domain exception messages.
- **Performance/volume:** Bounded request sizes (at most 100 offers and 20 media references per version) (assumption); list pages of at most 100 items served by keyset queries on `(product_id, created_at, id)` indexes; readiness stays linear in the number of offers.
- **Compatibility:** Contract-first OpenAPI 3.1 at `contracts/openapi/marketplace-api.yaml`, `info.version` 0.0.1 to 0.1.0, served under URL major version `/api/v1`; Phase 1 domain tests keep passing.
- **RESTful API design:** Every endpoint complies with the amended `contracts/convention/api-conventions.md` and RFC 9110: `/api/v{major}` base path, at most three nested resource levels, verb-free paths, cursor pagination with `cursor`, `limit` (default 20, maximum 100), and `sort`.
- **Rollout or migration:** Additive Flyway migrations V3 and V4 that succeed on clean and V2 databases; Hibernate `ddl-auto=validate`.

## Open Questions

None blocking.
All decisions from the interview on 2026-09-14 are recorded in the specification's decision log.
Two items are verifications rather than decisions, listed in the specification's Open Questions.

## User Approval

- [ ] I confirm the goal and scope.
- [ ] I confirm the success criteria.
- [ ] I confirm the main flow and critical extreme cases.
- [ ] The agent may create the detailed feature specification.
