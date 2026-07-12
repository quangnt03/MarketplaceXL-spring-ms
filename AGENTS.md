# Codex Implementation Rules

## Primary Instruction

This repository is the source of truth for the Marketplace Platform MVP. Implement only the marketplace MVP described here, in `artfacts/` and in `docs/md`. Do not add features outside scope without explicit instruction.

Project-scope naming is `marketplace`, not `erp`. Use:

- Maven parent artifact: `marketplace-platform`
- Backend module: `backend/marketplace-service`
- Java base package: `com.example.marketplace`
- Spring application class: `MarketplaceApplication`
- Frontend package: `marketplace-web`
- Contract files: `marketplace-api.yaml` and `marketplace-events.yaml`

Do not rename the project, packages, modules, or docs back to ERP naming.

## General Coding Principles

1. Never use the em dash character (Unicode `U+2014`).
   Use a plain dash (`-`) instead.
2. Never add an agent name as a commit co-author automatically.
3. Never manually modify `CHANGELOG.md` files or any files marked as auto-generated.
4. When writing or substantially editing long Markdown files, put each full sentence on its own line.
   Preserve normal Markdown structure, but do not wrap multiple sentences onto one physical line.
5. Do not give development cost much weight when making technical decisions.
   Prefer quality, simplicity, robustness, scalability, and long-term maintainability.
6. When fixing a bug, first reproduce it in an end-to-end setting that matches the end user's experience as closely as practical.
   Use that reproduction to identify the real problem and verify that the fix solves it.
7. When testing a product end to end, inspect the visible UI critically and aim for pixel-perfect quality.
   If something clearly looks wrong, fix it when it is within the task's authorized scope, even if it is not directly related to the current change.
8. Apply the same high standard to engineering quality, including lint errors, test failures, and flaky tests.
   Fix issues found within the task's authorized scope even when the current change did not cause them, and report out-of-scope issues clearly.

## Architecture Rules

1. Prefer simple implementation over unnecessary infrastructure.
2. Start with one Spring Boot service: `marketplace-service`.
3. Preserve package boundaries inside `marketplace-service` for marketplace domains:
   - `storefront`
   - `catalog`
   - `cart`
   - `checkout`
   - `orders`
   - `library`
   - `merchant`
   - `admin`
   - `payment`
   - `review`
   - `files`
   - `webhook`
   - `integration`
   - `platform`
4. Keep platform concerns under `platform`:
   - `auth`
   - `aws`
   - `cache`
   - `messaging`
   - `observability`
5. Do not create additional services unless explicitly requested.
6. Do not introduce Kafka, Elasticsearch, service mesh, Terraform, or distributed tracing in the MVP.

## Backend Rules

1. Use Spring Boot.
2. Use Spring Web for REST APIs.
3. Use Spring Security for authentication and authorization.
4. Use Spring Data JPA and Hibernate for Oracle persistence.
5. Use Flyway migrations for schema changes.
6. Use transactions for checkout, webhook processing, review creation, and product publication.
7. Enforce tenant isolation in the service layer.
8. Enforce role checks in backend APIs.
9. Use DTOs for request and response models.
10. Do not expose JPA entities directly in API responses.
11. Use validation annotations for request validation.

## Database Rules

1. Oracle is the source of truth.
2. Use `tenant_id` on tenant-owned tables.
3. Use unique constraints for:
   - store slug
   - product slug per tenant
   - webhook provider and event ID
   - access grant buyer and product
   - review buyer and product
4. Do not depend on Redis as the source of truth.

## Payment Rules

1. Create a pending order before payment confirmation.
2. Do not grant access during checkout creation.
3. Grant access only after a confirmed payment success event.
4. Webhook processing must be idempotent.
5. Duplicate webhook events must not create duplicate access grants.
6. Support mock payment mode for local development.

## Security Rules

1. Buyer can access only their own cart, orders, library, and reviews.
2. Merchant can access only their own tenant store and products.
3. Admin can access platform management APIs.
4. Private product files must not have permanent public URLs.
5. File download must check an active access grant.
6. Admin-only APIs must require the `ADMIN` role.

## Frontend Rules

1. Use Next.js and TypeScript.
2. Build these page groups:
   - public storefront
   - product detail
   - search
   - cart and checkout
   - buyer library
   - merchant dashboard
   - admin dashboard
3. Keep feature code under `frontend/web/src/features` using marketplace feature names.
4. Do not over-invest in advanced UI before backend flow works.
5. Use a typed API client where possible.

## Infrastructure Rules

1. Keep daily local development in `compose.yaml`.
2. Keep parity-only dependencies in `compose.parity.yaml`.
3. Keep LocalStack scripts under `infra/localstack`.
4. Keep Oracle initialization under `infra/oracle`.
5. Keep AWS and Kubernetes folders as placeholders until the local vertical slice works.

## Testing Rules

### Strategy

Use risk-based, test-first development. For every feature, derive tests from the
approved acceptance criteria in `artifacts/<feature-slug>/specification.md`.
Write the smallest relevant failing test, confirm that it fails for the expected
behavioral reason, implement the minimum code needed to pass, and then refactor
with the tests green.

Use a test pyramid:

1. Prefer many fast unit tests for domain rules, validation, calculations, and
   state transitions.
2. Add focused integration tests for Spring MVC, Spring Security, JPA, Flyway,
   transactions, tenant isolation, and adapter boundaries.
3. Add a small number of Playwright end-to-end tests for critical user journeys
   across the Next.js frontend and marketplace API.
4. Maintain smoke tests for application startup, health, and the minimum
   business-critical path used to validate a local or deployed environment.

Do not require every acceptance criterion to appear at every test layer. Assign
each criterion to the lowest layer that proves it reliably, then add a higher
layer only when component wiring or user-visible behavior creates additional
risk. Record `N/A` and the reason when unit, integration, end-to-end, or smoke
coverage does not apply.

### Risk Priorities

Prioritize these tests above broad coverage targets:

1. Tenant isolation tests
2. RBAC tests
3. Checkout transaction tests
4. Database ACID test
5. Webhook idempotency tests
6. Private file access tests
7. Review eligibility tests

For all tenant-owned behavior, include an allowed same-tenant case and a denied
cross-tenant case. For secured APIs, include unauthenticated, wrong-role,
wrong-owner, and allowed-role cases where applicable. For transaction and
webhook behavior, include duplicate, retry, concurrent, partial-failure, and
rollback cases when relevant.

### Test Layers

#### Unit Tests

1. Keep unit tests independent of the Spring application context where
   possible.
2. Test observable behavior rather than private methods or implementation
   details.
3. Use deterministic inputs and cover normal, boundary, invalid, and state
   transition cases.
4. Mock only direct collaborators; do not mock the class under test.

#### Integration Tests

1. Use focused Spring test slices when they prove the boundary; use a full
   application context only for flows that require it.
2. Verify authorization at the HTTP boundary and tenant isolation again in the
   service/persistence flow.
3. Verify transaction rollback and idempotency against real persistence
   behavior.
4. H2 may be used for fast tests that do not depend on Oracle semantics.
5. Flyway migrations, Oracle-specific SQL, indexes, constraints, locking, and
   transaction behavior require an Oracle-backed parity test. Do not claim
   Oracle compatibility from an H2-only test.
6. Mock external payment, file, messaging, and AWS boundaries unless the test
   explicitly targets the adapter. Keep mock payment mode available for local
   integration and end-to-end tests.

#### End-to-End Tests

1. Use Playwright under `frontend/web/e2e` for critical user-facing journeys.
2. Cover the happy path plus only the highest-risk permission or failure branch;
   keep detailed combinations in unit and integration tests.
3. Interact through user-visible roles and accessible selectors. Avoid brittle
   CSS selectors, fixed delays, shared mutable test state, and test-order
   dependencies.
4. Prepare deterministic users, tenants, products, carts, orders, and payment
   outcomes. Clean up or isolate test data so reruns are safe.
5. Never call a skipped placeholder an implemented end-to-end test.

#### Smoke Tests

1. Keep smoke tests short, read-only where possible, and safe to rerun.
2. Verify backend startup and health, frontend availability, and one minimal
   critical API or user path appropriate to the feature.
3. Smoke failures must identify which boundary failed; they are not a substitute
   for behavioral integration or end-to-end coverage.

### Traceability and Fixtures

1. Give acceptance criteria stable IDs such as `AC-01` and map every planned
   test to one or more IDs in the feature specification.
2. Use descriptive test names that state the condition and expected outcome;
   include the acceptance-criteria ID when practical.
3. Keep fixtures minimal and explicit. Builders may provide valid defaults, but
   each test must make the important tenant, role, ownership, state, and payment
   data visible.
4. Tests must be deterministic, independent, repeatable, and safe to run in any
   order or in parallel unless a documented infrastructure constraint prevents
   it.

### Execution and Completion Evidence

Run the narrowest relevant tests during the red-green-refactor loop, then run
all affected suites before declaring the feature complete. The standard entry
points are:

- Backend: Maven test/verify commands from the repository root or `backend`.
- Frontend E2E: `npm run test:e2e` from `frontend/web`.
- Frontend quality: the configured lint and build commands from `frontend/web`.
- Parity/smoke: the repository Compose environment required by the feature.

Do not claim that a test, suite, build, migration, or smoke check passes without
running the corresponding command and checking its current exit code and
output. If a required check cannot run, report it as unverified with the reason;
do not silently treat it as passed. A test is complete only when it fails for the
expected reason before implementation, passes afterward, and does not depend on
skips, focus flags, ignored assertions, or weakened expectations.

## Done Definition

A feature is done only when:

1. Backend API works.
2. Authorization is enforced.
3. Database migration exists.
4. Core tests exist. Database transactions are consistent for every IO operation
5. Frontend can call the API if the feature is user-facing.
6. Error cases are handled.
7. No out-of-scope feature was added.
