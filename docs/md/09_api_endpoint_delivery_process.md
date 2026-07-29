# API Endpoint Delivery Process

## Purpose

This process defines how to add or change an endpoint in the Marketplace Platform MVP.
It applies the API-first principle from the [Zalando RESTful API Guidelines](https://opensource.zalando.com/restful-api-guidelines/) and the project rules in `contracts/convention/api-conventions.md`.

The contract is designed, reviewed, and tested before the implementation is considered complete.
An endpoint is not done when only its controller exists.

## Inputs

Endpoint work starts from approved acceptance criteria in `artifacts/<feature-slug>/specification.md`.
Each acceptance criterion MUST have a stable identifier such as `AC-01`.
The proposal MUST identify:

- the consumers and user-visible journey;
- the owning marketplace domain and business owner;
- the resource and state transition being exposed;
- the allowed roles and tenant or ownership boundary;
- expected success, validation, authorization, conflict, and failure outcomes;
- transaction, retry, duplicate, concurrency, and rollback requirements;
- collection size, filtering, sorting, and pagination needs;
- compatibility impact on existing consumers;
- explicitly excluded behavior.

Use `contracts/convention/api_profile_example.md` as the proposal checklist.

## Delivery Flow

### 1. Confirm Scope and Acceptance Criteria

Read the feature specification and map the endpoint behavior to acceptance-criteria IDs.
Reject controller-shaped requests until the underlying resource or state transition is clear.
Confirm that the endpoint belongs to one of the existing `marketplace-service` domain packages.
Do not create a new service or add out-of-scope infrastructure.

Output:

- accepted scope and exclusions;
- acceptance-criteria mapping;
- named API consumers;
- unresolved business decisions.

### 2. Model the Resource

Define the resource, identifier, lifecycle, relationships, ownership, and tenant boundary.
Choose a plural, domain-specific, verb-free path.
Choose the HTTP method from the desired resource semantics.
For a business action, first try a new resource or a state transition on the existing resource.

Output:

- method and path;
- request and response resource shapes;
- state transition and invariants;
- authorization decision table.

### 3. Design Failure and Operational Behavior

List expected failure modes before writing the happy path.
Select precise HTTP status codes and stable RFC 9457 problem types.
Define validation behavior, safe error details, and existence-hiding rules.
Define retry semantics, idempotency, optimistic locking, transaction boundary, rollback behavior, and cache policy.
For collections, define deterministic ordering, filters, cursor behavior, and limits.

Output:

- success and error matrix;
- idempotency and concurrency contract;
- transaction and rollback contract;
- pagination and cache contract.

### 4. Change the OpenAPI Contract First

Edit `contracts/openapi/marketplace-api.yaml` before production code.
Keep the file self-contained and reuse local components.
Add:

- the path and operation with a stable `operationId`;
- tags, summary, and behavior-focused description;
- operation-level security;
- every path, query, and header parameter;
- request content and schema;
- every expected success and error response;
- `Location`, `ETag`, idempotency, rate-limit, or correlation headers when relevant;
- one representative success example and one representative failure example;
- schema constraints and required fields;
- the acceptance-criteria IDs in the operation description.

Increment `info.version` according to semantic versioning.
Use a minor increment for compatible functionality and a major increment only for an approved incompatible change.

Exit criteria:

- the YAML parses;
- the OpenAPI document validates;
- configured API lint checks pass;
- the contract is understandable without reading implementation code.

### 5. Review with Provider and Consumer

Request early review from:

- a backend owner for domain, transaction, security, and operability concerns;
- a consumer representative for usability and integration concerns;
- a security reviewer when authentication, authorization, private files, payment, or admin behavior changes.

Review the proposal against `contracts/convention/api-conventions.md`.
Resolve comments in the contract before implementation.
Record approved exceptions and unresolved decisions explicitly.

Exit criteria:

- provider and consumer agree on the contract;
- no unresolved decision blocks implementation;
- compatibility impact and migration plan are accepted.

### 6. Write the Smallest Failing Test

Derive tests from the acceptance criteria and assign each criterion to the lowest reliable test layer.
First reproduce the missing or incorrect behavior as close to the end-user path as practical.
Run the new test and confirm that it fails for the expected behavioral reason.

Use:

- unit tests for domain rules, validation, calculations, and state transitions;
- focused Spring integration tests for HTTP mapping, security, JPA, Flyway, and transactions;
- Oracle-backed parity tests for Oracle SQL, constraints, locking, and transaction semantics;
- Playwright tests for critical user-visible journeys;
- smoke tests for startup, health, and the minimum business-critical path.

Tenant-owned behavior MUST include same-tenant access and denied cross-tenant access.
Secured behavior MUST cover unauthenticated, wrong-role, wrong-owner, and allowed cases where applicable.
Duplicate-sensitive or transactional behavior MUST cover the relevant duplicate, retry, concurrency, partial-failure, and rollback cases.

Evidence:

- test name and acceptance-criteria ID;
- command that produced the expected failure;
- failure reason.

### 7. Implement Through the Domain Boundary

Implement only enough behavior to satisfy the reviewed contract and failing tests.
Keep:

- controllers responsible for HTTP mapping and DTO validation;
- services responsible for authorization, tenant isolation, business rules, and transaction boundaries;
- repositories responsible for persistence;
- external payment, file, messaging, and AWS behavior behind adapters;
- JPA entities out of API responses.

Add a Flyway migration for schema changes.
Use database constraints for required uniqueness and service checks for clear client errors.
Do not trust identifiers in the request as proof of caller identity or tenant membership.

### 8. Verify Contract and Implementation

Run the narrowest relevant test during red-green-refactor.
Then run all affected suites and check their current exit codes.
Verify:

- documented success and failure examples against the running API;
- request and response media types;
- headers and status codes;
- authentication, role, ownership, and tenant isolation;
- idempotent retry and duplicate behavior;
- transaction rollback and persistence constraints;
- frontend integration for user-facing endpoints;
- visible UI behavior with Playwright for user-facing changes;
- generated or runtime API behavior against the OpenAPI contract where tooling exists.

Do not report a suite, build, migration, or smoke check as passing unless it was run.
Report anything that could not run as unverified with its reason.

### 9. Publish and Operate

Publish the OpenAPI specification with the deployed service.
Update `docs/md/04_api_contract.md` when the endpoint inventory or business validation summary changes.
Add consumer-facing examples or repair guidance when OpenAPI alone is insufficient.
Monitor status classes, latency, authorization failures, and domain-specific failure rates using bounded labels.

For a deprecated endpoint:

- mark it deprecated in OpenAPI;
- identify active consumers;
- agree on a replacement and migration window;
- add `Deprecation` and `Sunset` headers when applicable;
- remove it only after consumer approval and usage verification.

## Pull Request Evidence

An endpoint pull request MUST include:

- acceptance-criteria IDs and scope;
- contract diff and semantic version change;
- resource and authorization rationale;
- compatibility assessment;
- failing-test evidence from before implementation;
- passing commands and exit status after implementation;
- migration and rollback notes when persistence changes;
- screenshots or Playwright evidence for user-facing behavior;
- any unverified checks and the reason;
- approved convention exceptions.

## Definition of Done

The endpoint is done only when:

- the reviewed OpenAPI contract is the source of truth;
- backend behavior matches the contract;
- authorization, ownership, and tenant isolation are enforced;
- required transaction and database migration behavior exists;
- core tests pass without skips or weakened assertions;
- the frontend consumes the endpoint when it is user-facing;
- error, duplicate, retry, concurrency, and rollback cases are handled as applicable;
- the contract is validated and publishable;
- no out-of-scope feature was introduced.

