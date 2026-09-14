# TODO: Product Lifecycle Application Integration (Phase 2)

Source of truth: [specification.md](./specification.md) (sections 12 and 13) and [intake.md](./intake.md).
Each sub-phase ends with one quality gate.
Commit only when every box of that gate is ticked; the agent provides the commit message, the developer commits.
If implementation reveals a requirement or design change, update `intake.md` and `specification.md` before continuing, in the same sub-phase.

## Standard Quality Gate

Every sub-phase gate includes these checks, plus the sub-phase specific ones listed with it.
Commands run from `backend/` unless stated otherwise.

- [ ] **Scoped tests green:** the sub-phase's test command passes with its current exit code checked.
- [ ] **Full regression green:** `./gradlew check` passes (all tests, Checkstyle, PMD, SpotBugs for main and test sources, Spring Modulith verification).
- [ ] **No lint findings:** Checkstyle `maxWarnings = 0`, PMD, and SpotBugs report nothing new; no new `@SuppressWarnings` without a written reason.
- [ ] **Honest tests:** no `@Disabled`, skipped, focused, or commented-out tests; no weakened assertions; every new behavioral test was seen failing for the expected reason before the implementation.
- [ ] **Scoped diff:** `git status` and `git diff --cached` contain only this sub-phase's files; unrelated changes are stashed or left unstaged.
- [ ] **Docs in sync:** spec, intake, and this TODO reflect any decision or deviation made during the sub-phase; no em dash characters.

Commands that need Docker (Testcontainers Oracle) or build the service image can exceed 5 minutes on a first run; the developer runs those.

## Prerequisites

- [ ] Developer: Docker running; Java 25 toolchain available; `gvenzl/oracle-free` image pulled once (long run, about 1-2 GB).
- [ ] Developer: `./gradlew check` is green on the current branch before P2.1 starts, so later failures are attributable to this phase.
- [ ] Agent: if the baseline is red, report the failing checks with output and propose fixes before any Phase 2 work.

---

## P2.0 Documentation baseline

Agent tasks:

- [x] Write Phase 2 `intake.md` and `specification.md` from the templates.
- [x] Move Phase 1 artifacts to `core-domain/` and fix relative links.
- [x] Amend `contracts/convention/api-conventions.md`, align `api_profile_example.md` and `docs/md/07_testing_plan.md`.
- [x] Update the artifact path in `AGENTS.md`.
- [x] Write this TODO.

Developer tasks:

- [ ] Review and tick the approval boxes in `intake.md`.
- [ ] Commit the staged documentation.

Quality gate:

- [ ] All relative links in `artifacts/product-publication-lifecycle/` resolve (known pre-existing exception: `docs/diagrams/class.mmd`).
- [ ] No em dash characters in changed files.
- [ ] Intake approval boxes ticked.

Commit: `docs(product-lifecycle): specify application integration phase and amend API conventions`

---

## P2.1 OpenAPI contract 0.1.0

Agent tasks:

- [ ] Draft `contracts/openapi/marketplace-api.yaml`: `info` fields required by the conventions, `servers` base path `/api/v1`, `basicAuth` scheme marked dev/test-only.
- [ ] Add the eleven operations from spec section 6 with stable `operationId`, tags, AC IDs in descriptions, and operation-level security.
- [ ] Add component schemas for every DTO, `Money`, `Pagination`, and RFC 9457 `Problem` with `errors[]` and extensions.
- [ ] Add headers: `X-Flow-ID`, `ETag`, `If-Match` (required on `PUT /product-versions/{version_id}`), `Location`, `Retry-After`, `Cache-Control`.
- [ ] Add one success and one failure example per operation, and every error response listed in section 6.

Developer tasks:

- [ ] Review the contract against `api-conventions.md` and the spec; approve or request changes.

Quality gate:

- [ ] From the repository root: `npx @redocly/cli lint contracts/openapi/marketplace-api.yaml` exits 0.
- [ ] Every operation has a success example, a failure example, and all section 6 error responses.
- [ ] Every path uses `/api/v1`, nests at most three resource levels, and contains no verbs.
- [ ] Standard gate (no backend code changed, so `./gradlew check` stays as baseline).

Commit: `docs(contracts): define product lifecycle API 0.1.0`

---

## P2.2 Domain changes

Agent tasks:

- [ ] Write failing unit tests UT-01 (restore factories), UT-02 (inventory init), UT-03 (DRAFT guard), UT-04 (null-safe readiness), UT-05 (empty offers at publish), UT-06 (one-version rule).
- [ ] Run them and record that each fails for the intended reason.
- [ ] Review the developer's implementation against section 8 "Required domain changes".

Developer tasks:

- [ ] Add `restore(...)` factories on `Product`, `ProductVersion`, `ProductVariant`, `ProductVariantVersion`, `Inventory`.
- [ ] Add the `DRAFT` guard on `ProductVersion.updateDetails` and offer replacement (`InvalidStatusOperationException`).
- [ ] Fix `ProductVariant.initializeInventory`: reserved 0, use `reorderLevel`.
- [ ] Make SKU and display-name readiness checks null-safe.
- [ ] Make `Product.publish` with no offers throw `ProductReadinessException(NO_ACTIVE_VARIANT)`.
- [ ] Add the one-version rule on `Product` (`InvalidStatusOperationException`, entity `product`, operation `product_version_create`).

Quality gate:

- [ ] `./gradlew test --tests "com.example.marketplace.product*" --tests "com.example.marketplace.inventory*"` green, including all Phase 1 tests.
- [ ] Domain classes still import no Spring or JPA types.
- [ ] Standard gate.

Commit: `feat(product): add rehydration factories and fix lifecycle domain defects`

---

## P2.3 Test infrastructure

Agent tasks:

- [ ] Add test dependencies: `spring-boot-testcontainers`, Testcontainers JUnit Jupiter and Oracle Free modules, Boot 4 slice test starters (`spring-boot-starter-webmvc-test`, `spring-boot-starter-data-jpa-test`), `swagger-request-validator`.
- [ ] Add the singleton Oracle Free container support (`@ServiceConnection`) and the `LifecycleTestData` JDBC seeding helper (tenants, users, roles `STORE_MEMBER`/`STORE_ADMIN`, stores, memberships, categories).
- [ ] Add one container smoke test that starts Oracle and runs Flyway V1-V2.
- [ ] Verify V-02 (OpenAPI 3.1 support of the validator) with a spike test against the P2.1 contract; if unsupported, switch to `networknt json-schema-validator` and update spec section 12.

Developer tasks:

- [ ] Review dependency choices and versions.

Quality gate:

- [ ] Container smoke test green (Docker required).
- [ ] V-02 outcome recorded in spec section 1 "Open Questions".
- [ ] Standard gate.

Commit: `test(backend): add Oracle Testcontainers and contract validation infrastructure`

---

## P2.4 Flyway migrations

Agent tasks:

- [ ] Write failing IT-01 (clean database to latest, boot with `ddl-auto=validate`, and one boot without V4 to verify V-01) and IT-02 (V2 with legacy rows to latest).
- [ ] Set `spring.jpa.hibernate.ddl-auto=validate` and `spring.jpa.open-in-view=false` in all profiles.
- [ ] Review the developer's DDL against spec section 7.

Developer tasks:

- [ ] Write `V3__product_lifecycle_integration.sql` (constraints, conditional checks, function-based SKU index, keyset and lookup indexes, `row_version`, price columns, nullable draft columns, recovery notes in the header).
- [ ] Write `V4__event_publication.sql` matching the Spring Modulith 2.1 JPA mapping for Oracle.

Quality gate:

- [ ] `./gradlew test --tests "*MigrationIT"` green.
- [ ] V-01 outcome recorded in spec section 1; if V4 is not needed, record the follow-up instead of silently dropping it.
- [ ] Standard gate.

Commit: `feat(db): add product lifecycle integration migrations`

---

## P2.5 JPA entities and mappers

Agent tasks:

- [ ] Add JPA entities in `product.persistence` for products, product versions, version images, variants, variant versions, and inventory items (`@Version` on products and versions).
- [ ] Add entity-to-domain mappers that use only `restore(...)` factories and getters.
- [ ] Add Spring Data repository interfaces without custom queries.

Developer tasks:

- [ ] Review mappings (UUID `RAW(16)`, enum strings, `Money` columns, image rows).

Quality gate:

- [ ] IT-01 still green, proving every entity validates against the migrated schema.
- [ ] ARCH-01 green (no entity referenced outside `product.persistence`).
- [ ] Standard gate.

Commit: `feat(product): add JPA entities and mappers for lifecycle persistence`

---

## P2.6 Repository adapter

Agent tasks:

- [ ] Write failing IT-03 (round trip), IT-04 (unique constraints), IT-05 (inventory constraints), IT-06 (conditional offer constraints), IT-07 (optimistic lock), IT-08 (keyset pages, ties, both directions, filters, insert between pages).
- [ ] Review the developer's adapter.

Developer tasks:

- [ ] Implement `ProductLifecycleRepository`: aggregate graph load and save, unlocked read, `PESSIMISTIC_WRITE` read with bounded lock timeout, keyset page queries for variants and versions.

Quality gate:

- [ ] `./gradlew test --tests "*RepositoryIT"` green.
- [ ] Standard gate.

Commit: `feat(product): implement lifecycle repository adapter`

---

## P2.7 Platform: authentication, access, observability

Agent tasks:

- [ ] Add `CurrentActor`, `CurrentActorResolver`, and `StubCurrentActorResolver` (active only with `marketplace.auth.mode=stub`), stub users with `platform_roles` in dev and test configuration.
- [ ] Update `SecurityConfiguration`: HTTP Basic in stub mode, fail closed otherwise, platform `MERCHANT` required on `/api/v1/**`, startup failure when `stub` is combined with `prod`.
- [ ] Add `StoreAccessQuery` (active membership and store role code).
- [ ] Add problem+json `AuthenticationEntryPoint` and `AccessDeniedHandler`.
- [ ] Add `FlowIdFilter`, ECS structured logging in all profiles, and fix the `readinesss` health group typo.
- [ ] Add `package-info.java` with `@NamedInterface("auth")` (and `observability` if referenced cross-module).
- [ ] Write platform tests: 401 and 403 through a test-only endpoint (platform part of CT-02), `FlowIdFilterTest` (CT-05), stub-with-prod startup failure, `StoreAccessQuery` against Oracle; add their IDs to spec section 12.

Developer tasks:

- [ ] Security review: fail-closed behavior, no credentials in logs, prod guard, trust boundary of `CurrentActorResolver`.

Quality gate:

- [ ] `./gradlew test --tests "com.example.marketplace.platform*"` green.
- [ ] `/actuator/health/readiness` includes `db` in a boot test.
- [ ] ARCH-01 green with the new named interfaces.
- [ ] Standard gate.

Commit: `feat(platform): add stub authentication, store access query, and structured logging`

---

## P2.8 Application service

Agent tasks:

- [ ] Add `ProductLifecycleService` method signatures, command records, and `ResourceNotFoundException`.
- [ ] Write failing IT-09 (publish), IT-10 (rollback), IT-11 (concurrency), IT-12 (inactive product and repeats), IT-13 (membership and role matrix, no lock wait for non-members), IT-14 (lock timeout), IT-15 (structured lifecycle log events).
- [ ] Review the developer's implementation.

Developer tasks:

- [ ] Implement service bodies: one transaction per command, unlocked read and authorization (404, then 403), product lock and re-read, `If-Match` check, domain calls, save, structured log event after commit, read-only list and get operations.

Quality gate:

- [ ] `./gradlew test --tests "*ServiceIT"` green, including IT-11 repeated 20 times without flakiness.
- [ ] Standard gate.

Commit: `feat(product): implement lifecycle use cases with authorization before locking`

---

## P2.9 Web layer

Agent tasks:

- [ ] Add request/response DTOs with structural validation, including the single-primary-media cross-field rule and list query validation.
- [ ] Add `CursorCodec` (opaque, integrity-protected, bound to filters and sort).
- [ ] Add controllers for the eleven operations under `/api/v1`, `ETag`/`If-Match` handling, `Location`, `Cache-Control`.
- [ ] Add `ProductProblemHandler` covering every row of the section 6 error table, including 428 and 503 with `Retry-After`.
- [ ] Write UT-07 to UT-10 and CT-01 to CT-06 with contract validation on every response.

Developer tasks:

- [ ] Review that controllers hold no business rules and DTO checks stay structural.

Quality gate:

- [ ] `./gradlew test --tests "com.example.marketplace.product.web*"` green.
- [ ] Every CT response validates against the P2.1 contract; contract and implementation mismatches are fixed in the same sub-phase (contract changes need developer approval).
- [ ] Standard gate.

Commit: `feat(product): add lifecycle REST controllers, DTOs, and problem mapping`

---

## P2.10 E2E API tests

Agent tasks:

- [ ] Add the shared `LifecycleApiScenario` (main flow steps as reusable calls with contract validation).
- [ ] Write E2E-01 to E2E-06 on `RANDOM_PORT` with the real filter chain and Oracle.

Developer tasks:

- [ ] Review scenarios against the intake's main flow and failure cases.

Quality gate:

- [ ] `./gradlew test --tests "*E2EIT"` green.
- [ ] Standard gate.

Commit: `test(product): add lifecycle E2E API tests`

---

## P2.11 Packaged component test

Agent tasks:

- [ ] Write CE-01 `ProductLifecycleContainerComponentIT`: build the image from `backend/marketplace-service/Dockerfile`, run it next to an Oracle container on a shared network, wait on readiness, run `LifecycleApiScenario` with contract validation, plus one readiness failure case.
- [ ] Write `scripts/component-smoke.sh` for manual ST-01 against any running instance.

Developer tasks:

- [ ] Run CE-01 (long run: builds the service image).
- [ ] Run ST-01 manually once: Oracle from `compose.parity.yaml`, `bootRun` with `dev`, then the script.

Quality gate:

- [ ] `./gradlew test --tests "*ContainerComponentIT"` green.
- [ ] ST-01 succeeds and readiness reports `db` UP.
- [ ] Standard gate.

Commit: `test(product): add packaged-image component test and smoke script`

---

## P2.12 Regression and handoff

Agent tasks:

- [ ] Write the handoff: files changed, behavior changed, assumptions, decisions, tests and validation run, risks and unverified items, items for human review.
- [ ] Report deployment blockers: stub authentication, `compose.yaml` Oracle dependency, CI without contract lint and Docker-backed tests.
- [ ] Tick the spec's Definition of Done boxes that are proven and set the spec status accordingly.

Developer tasks:

- [ ] Run all verification commands in spec section 12 and confirm exit codes.
- [ ] Decide CI follow-ups (contract lint job, Docker-backed test job) and `compose.yaml` Oracle wiring; these are developer-owned.

Quality gate:

- [ ] All section 12 verification commands green.
- [ ] Every Definition of Done box in spec section 14 ticked, or unticked with a recorded reason.
- [ ] Standard gate.

Commit: `docs(product-lifecycle): record Phase 2 verification and handoff`
