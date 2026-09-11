# Codex Implementation Rules

## Primary Instruction

This repository is the source of truth for the Marketplace Platform MVP. Implement only the marketplace MVP described here, in `artfacts/` and in `docs/md`. Do not add features outside scope without explicit instruction.

Project-scope naming is `marketplace`, not `erp`. Use:

- Gradle root project: `marketplace-platform`
- Backend module: `backend/marketplace-service`
- Java base package: `com.example.marketplace`
- Spring application class: `MarketplaceApplication`
- Frontend package: `marketplace-web`
- Contract files: `marketplace-api.yaml` and `marketplace-events.yaml`

Do not rename the project, packages, modules, or docs back to ERP naming.

## Agent Role and Scope

This is a learning project. Maximizing the user's learning value takes priority over
implementation speed, and it overrides the general Delegation Tiers below wherever the
two disagree.

1. An agent's direct-implementation responsibility is limited to: bootstrapping
   boilerplate, boring templates (DTOs, mappers, getters/setters, routine config),
   writing tests as explicitly instructed, summarizing logs and command output, and
   writing supporting scripts or CLI tooling.
2. An agent must never directly implement a crucial business logic path, a core
   service, a CI/CD gate, or infrastructure, even Tier 2 items normally allowed by the
   Delegation Tiers below and even if explicitly asked in the moment. Instead, an agent
   suggests an approach, verifies and reviews the user's own implementation, writes
   tests against it, and actively challenges the design when something looks wrong or
   risky. If asked to implement one of these directly, say so and offer the
   suggest/verify/test/challenge alternative instead.
3. The user's own implementations and design decisions in this repository are the
   reference for best practice. Read the user's code and prior decisions before writing
   anything adjacent to them, follow the patterns already established (for example the
   state-transition pattern on `Product`/`ProductVersion`/`ProductVariant`/
   `ProductVariantVersion`, and the Spring Modulith module boundaries), and carry that
   style forward into other projects rather than introducing new idioms of the agent's
   own.
4. Keep this repository usable by any AI coding agent, not only Claude Code.
   `AGENTS.md` is the single canonical, tool-agnostic instructions file. Tool-specific
   filenames (`CLAUDE.md`, `GEMINI.md`, and similar) must stay symlinks to `AGENTS.md`
   rather than forks with drifting content. Prefer scripts, configuration, and
   documentation that any agent or a human can run directly over features that depend
   on one vendor's proprietary agent tooling, unless the user explicitly asks for a
   vendor-specific integration.

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

## Delegation and Human Ownership

Use agents to remove typing, repetition, translation between representations, scaffolding, and mechanical refactoring.
Keep requirements, tradeoffs, architecture, invariants, failure analysis, debugging judgment, and final technical judgment under human ownership.

### Delegation Tiers

1. Tier 1 is human-owned decision work.
   This includes requirements and acceptance criteria, domain and aggregate boundaries, state transitions, transaction and concurrency design, API and event contracts, database ownership, security and tenant-isolation models, technology choices, deployment topology, infrastructure architecture, test strategy, failure and retry semantics, and root-cause conclusions.
   An agent may gather evidence, critique a design, identify failure modes, and present options with tradeoffs.
   An agent must not choose or change a Tier 1 decision unless the user explicitly instructs it or an approved project artifact already records the decision.
2. Tier 2 is shared implementation work.
   This includes core feature logic, business services, repository queries, caching behavior, integration tests, the first meaningful implementation of an important concept, and implementation of an approved infrastructure or delivery design.
   An agent may scaffold, review, and implement bounded Tier 2 work after the relevant Tier 1 decisions and invariants are fixed.
   Preserve a user-designated learning or manual zone, and do not take over its first meaningful implementation unless the user explicitly delegates it.
   In this repository, "Agent Role and Scope" above narrows this further: core feature logic, business services, and core services stay user-implemented; an agent's role on those items is scaffolding, review, tests, and challenge, not direct implementation, regardless of delegation otherwise.
3. Tier 3 is delegatable mechanical work.
   This includes DTOs, mappers, repetitive repository methods, controller scaffolding, validation annotations, exception-response boilerplate, test fixtures, repetitive test setup, routine configuration, documentation drafts, import and package updates, and mechanical refactoring.
   An agent may implement Tier 3 work end to end, but must still inspect the surrounding context, validate the result, and flag anything that needs human review.

### Delegation Gates

1. Classify each requested work item by tier before implementation.
   The classification may remain implicit when every item is clearly Tier 3.
2. Treat explicit user instructions and approved specifications, contracts, architecture decision records, and diagrams as established decisions.
3. If a required Tier 1 decision is unresolved, pause only the affected work, present focused options and a recommendation, and request the decision.
   Continue independent in-scope work that does not depend on that decision.
4. Do not silently change:
   - aggregate, domain, module, or service boundaries
   - database schema ownership or persistence technology
   - public API contracts or event schemas
   - transaction, concurrency, idempotency, failure, or retry semantics
   - authentication, authorization, tenant isolation, secrets, or trust boundaries
   - deployment topology, infrastructure architecture, or technology choices
5. For important or high-risk work, use the sequence: human or approved-artifact design, agent critique, bounded implementation, automated validation, agent review, and human final judgment.
6. For debugging, reproduce the user-visible failure and collect evidence before proposing a cause.
   An explicit request to fix the bug authorizes a bounded implementation, but the handoff must explain the evidence, root cause, and why the fix addresses it.
7. Never leave the repository with code that the handoff cannot explain or with complexity that is unnecessary for the approved design.

### Required Delegation Handoff

After making changes, report the following concisely:

1. Files changed
2. Behavior changed
3. Assumptions made
4. Architectural decisions used or proposed
5. Tests added and validation run
6. Risks and unverified items
7. Items requiring human review

The user may opt into the repository skill `$delegation-governance` for an explicit delegation plan, decision audit, manual-zone boundary, or risk-based review contract.
When invoked, follow it in addition to these always-on rules.

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

- Backend: Gradle test/check commands through the wrapper in `backend`.
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

## Development Commands

### Backend (Gradle, run from `backend/`)

- `./gradlew check` - full verification: tests plus the Spring Modulith module-boundary check. This is what CI runs (as `./gradlew check :marketplace-service:bootJar`).
- `./gradlew test` - unit/integration tests only.
- `./gradlew test --tests "com.example.marketplace.product.ProductPublicationLifecycleTest"` - run a single test class.
- `./gradlew test --tests "com.example.marketplace.product.ProductPublicationLifecycleTest.methodName"` - run a single test method.
- `./gradlew :marketplace-service:bootRun` - run the service locally.
- `./gradlew :marketplace-service:bootJar` - build the runnable jar.
- Java 25 is required; the Gradle toolchain (`backend/build.gradle`) enforces this automatically. Gradle is the only JVM build tool, do not introduce Maven.

### Frontend (pnpm, run from `frontend/web/`)

- `pnpm install` - install dependencies.
- `pnpm dev` - dev server.
- `pnpm build` - production build.
- `pnpm lint` - `next lint`.
- `pnpm test:e2e` - Playwright end-to-end tests (config/specs under `frontend/web/e2e`).
- CI (`.github/workflows/ci.yml`) also runs `pnpm test:unit`, but no such script exists yet in `frontend/web/package.json`; add it when frontend unit tests are introduced rather than assuming it already works.

### Local infrastructure

- Copy `.env.example` to `.env.local`, then `docker compose --env-file .env.local up --build` (frontend on `:3000`, backend on `:8080`).
- Helper scripts: `scripts/dev-up.sh`, `scripts/dev-status.sh`, `scripts/smoke-local.sh`, `scripts/dev-down.sh`.
- `compose.yaml` holds daily local services (Oracle, LocalStack, backend, frontend); `compose.parity.yaml` adds Redis and RabbitMQ for parity-only testing.

## Architecture Overview

- Gradle multi-project workspace (`backend/settings.gradle`, root project `marketplace-platform`) with a single deployable module today: `backend/marketplace-service` (Spring Boot 4.1, Java 25).
- `marketplace-service` is a **Spring Modulith** modular monolith, not a set of separate services. Each marketplace domain listed in "Architecture Rules" above is meant to be a top-level package under `com.example.marketplace`, declared with a `package-info.java` carrying `@ApplicationModule`. Modules may only depend on each other through explicitly published named interfaces and application events, never through internal classes.
- `ModularMonolithArchitectureTests` (`backend/marketplace-service/src/test/java/com/example/marketplace/architecture/ModularMonolithArchitectureTests.java`) calls `ApplicationModules.of(MarketplaceApplication.class).verify()`. This enforces the module boundaries above at test time, run it (via `./gradlew check` or `./gradlew test`) after adding or rewiring any cross-package dependency.
- `shared` is declared `@ApplicationModule(type = ApplicationModule.Type.OPEN)`, meaning it is visible to every other module. Put cross-cutting primitives there (for example `shared.exception.InvalidStateTransitionException`, `money.Money`), not domain-specific logic.
- Current domain code implements the **product publication lifecycle**: `Product`, `ProductVersion`, `ProductVariant`, `ProductVariantVersion`, each paired with its own `E*Status` enum and its own package. Follow the existing pattern for stateful entities: private setters, a `create(...)` static factory, and explicit transition methods (e.g. `discontinue()`, `resumeSelling()`, `archive()`) that validate the allowed source states and throw `InvalidStateTransitionException` on an illegal transition, rather than exposing a generic `setStatus`.
- `Tenant` and `Store` are the other domain aggregates currently implemented; tenant isolation and store ownership are foundational to every later module (see Security Rules and Database Rules above).
- The authoritative design for the currently active feature is `artifacts/product-publication-lifecycle/specification.md` plus its Mermaid diagrams in the same directory. Cross-feature system design lives in `docs/md/` (`02_system_architecture.md` for the service/data topology, `05_transactions_and_state.md` for transactional and state-machine behavior, `07_testing_plan.md`, `09_api_endpoint_delivery_process.md`, etc.).
- API and event contracts are versioned independently of code under `contracts/`: `contracts/openapi/marketplace-api.yaml`, `contracts/asyncapi/marketplace-events.yaml`, `contracts/webhooks/signature-contracts.md`, and conventions in `contracts/convention/api-conventions.md`.
- Frontend (`frontend/web`, Next.js + TypeScript) has no feature code yet; when adding pages, mirror the backend domain names under `frontend/web/src/features` as described in Frontend Rules above.
- Feature work is expected to follow the artifact-driven workflow described in `artifacts/README.md`: `intake.md` -> `specification.md` -> tests-first -> implementation, with every acceptance criterion traceable to a test.

<!-- rtk-instructions v2 -->
# RTK (Rust Token Killer) - Token-Optimized Commands

## Golden Rule

**Always prefix commands with `rtk`**. If RTK has a dedicated filter, it uses it. If not, it passes through unchanged. This means RTK is always safe to use.

**Important**: Even in command chains with `&&`, use `rtk`:
```bash
# ❌ Wrong
git add . && git commit -m "msg" && git push

# ✅ Correct
rtk git add . && rtk git commit -m "msg" && rtk git push
```

## RTK Commands by Workflow

### Build & Compile (80-90% savings)
```bash
rtk cargo build         # Cargo build output
rtk cargo check         # Cargo check output
rtk cargo clippy        # Clippy warnings grouped by file (80%)
rtk tsc                 # TypeScript errors grouped by file/code (83%)
rtk lint                # ESLint/Biome violations grouped (84%)
rtk prettier --check    # Files needing format only (70%)
rtk next build          # Next.js build with route metrics (87%)
```

### Test (60-99% savings)
```bash
rtk cargo test          # Cargo test failures only (90%)
rtk go test             # Go test failures only (90%)
rtk jest                # Jest failures only (99.5%)
rtk vitest              # Vitest failures only (99.5%)
rtk playwright test     # Playwright failures only (94%)
rtk pytest              # Python test failures only (90%)
rtk rake test           # Ruby test failures only (90%)
rtk rspec               # RSpec test failures only (60%)
rtk test <cmd>          # Generic test wrapper - failures only
```

### Git (59-80% savings)
```bash
rtk git status          # Compact status
rtk git log             # Compact log (works with all git flags)
rtk git diff            # Compact diff (80%)
rtk git show            # Compact show (80%)
rtk git add             # Ultra-compact confirmations (59%)
rtk git commit          # Ultra-compact confirmations (59%)
rtk git push            # Ultra-compact confirmations
rtk git pull            # Ultra-compact confirmations
rtk git branch          # Compact branch list
rtk git fetch           # Compact fetch
rtk git stash           # Compact stash
rtk git worktree        # Compact worktree
```

Note: Git passthrough works for ALL subcommands, even those not explicitly listed.

### GitHub (26-87% savings)
```bash
rtk gh pr view <num>    # Compact PR view (87%)
rtk gh pr checks        # Compact PR checks (79%)
rtk gh run list         # Compact workflow runs (82%)
rtk gh issue list       # Compact issue list (80%)
rtk gh api              # Compact API responses (26%)
```

### JavaScript/TypeScript Tooling (70-90% savings)
```bash
rtk pnpm list           # Compact dependency tree (70%)
rtk pnpm outdated       # Compact outdated packages (80%)
rtk pnpm install        # Compact install output (90%)
rtk npm run <script>    # Compact npm script output
rtk npx <cmd>           # Compact npx command output
rtk prisma              # Prisma without ASCII art (88%)
rtk uv run <cmd>        # Compact uv project command output
```

### Files & Search (60-75% savings)
```bash
rtk ls <path>           # Tree format, compact (65%)
rtk read <file>         # Code reading with filtering (60%)
rtk grep <pattern>      # Search grouped by file (75%). Format flags (-c, -l, -L, -o, -Z) run raw.
rtk find <pattern>      # Find grouped by directory (70%)
```

### Analysis & Debug (70-90% savings)
```bash
rtk err <cmd>           # Filter errors only from any command
rtk log <file>          # Deduplicated logs with counts
rtk json <file>         # JSON structure without values
rtk deps                # Dependency overview
rtk env                 # Environment variables compact
rtk summary <cmd>       # Smart summary of command output
rtk diff                # Ultra-compact diffs
```

### Infrastructure (85% savings)
```bash
rtk docker ps           # Compact container list
rtk docker images       # Compact image list
rtk docker logs <c>     # Deduplicated logs
rtk kubectl get         # Compact resource list
rtk kubectl logs        # Deduplicated pod logs
```

### Network (65-70% savings)
```bash
rtk curl <url>          # Compact HTTP responses (70%)
rtk wget <url>          # Compact download output (65%)
```

### Meta Commands
```bash
rtk gain                # View token savings statistics
rtk gain --history      # View command history with savings
rtk discover            # Analyze Claude Code sessions for missed RTK usage
rtk proxy <cmd>         # Run command without filtering (for debugging)
rtk init                # Add RTK instructions to CLAUDE.md
rtk init --global       # Add RTK to ~/.claude/CLAUDE.md
```

## Token Savings Overview

| Category | Commands | Typical Savings |
|----------|----------|-----------------|
| Tests | vitest, playwright, cargo test | 90-99% |
| Build | next, tsc, lint, prettier | 70-87% |
| Git | status, log, diff, add, commit | 59-80% |
| GitHub | gh pr, gh run, gh issue | 26-87% |
| Package Managers | pnpm, npm, npx | 70-90% |
| Files | ls, read, grep, find | 60-75% |
| Infrastructure | docker, kubectl | 85% |
| Network | curl, wget | 65-70% |

Overall average: **60-90% token reduction** on common development operations.
<!-- /rtk-instructions -->