# REST API Design Profile

Design the REST API for: `[business capability]`

Use this profile before changing `contracts/openapi/marketplace-api.yaml`.
Follow `contracts/convention/api-conventions.md` and `docs/md/09_api_endpoint_delivery_process.md`.

## Standards

- [ ] Follow the Zalando RESTful API Guidelines.
- [ ] Produce an OpenAPI 3.1 contract.
- [ ] Follow RFC 9110 for HTTP semantics.
- [ ] Use RFC 9457 Problem Details for error responses.
- [ ] Reuse Marketplace common schemas and conventions.

## Scope

- [ ] Consumers: [systems or user groups]
- [ ] Business owner: [owner]
- [ ] Owning domain package: [package]
- [ ] Acceptance criteria: [AC IDs]
- [ ] Included use cases: [list]
- [ ] Excluded use cases: [list]
- [ ] Owned resources: [list]
- [ ] Referenced external resources: [list]

## Required Deliverables

1. Resource model and resource relationships.
2. OpenAPI contract containing every operation.
3. Request, response and error schemas.
4. Authorization requirement for each operation.
5. Filtering, sorting and pagination behavior.
6. Idempotency and concurrency behavior where relevant.
7. At least one successful and one failure example per operation.
8. Backward compatibility assessment.
9. List of unresolved design decisions.

## Acceptance Criteria

- [ ] Paths use plural nouns and contain no operation verbs.
- [ ] Paths are unversioned and do not use `/api` as a base path.
- [ ] Path segments use kebab-case.
- [ ] Query parameters and JSON properties use snake_case.
- [ ] HTTP methods and status codes follow RFC 9110.
- [ ] All inputs and outputs have explicit schemas.
- [ ] Domain schemas reuse approved common primitives.
- [ ] Errors use application/problem+json.
- [ ] Collection operations follow the standard pagination format.
- [ ] Sensitive information is not exposed in responses or errors.
- [ ] Role, ownership, and tenant isolation rules are explicit.
- [ ] Retry, idempotency, concurrency, and rollback behavior are explicit where relevant.
- [ ] Tests map to acceptance-criteria IDs and fail for the expected reason before implementation.
- [ ] The contract passes schema validation and organizational linting.
- [ ] A consumer representative and API reviewer approve the design.
