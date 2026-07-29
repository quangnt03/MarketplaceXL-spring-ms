# Marketplace REST API Conventions

## Purpose and Authority

This document defines the default conventions for HTTP APIs owned by `marketplace-service`.
It adapts the [Zalando RESTful API Guidelines](https://opensource.zalando.com/restful-api-guidelines/) to the Marketplace Platform MVP.
RFC 2119 requirement words such as MUST, SHOULD, and MAY indicate the strength of each rule.
When this document and an endpoint-specific design disagree, this document wins unless the exception is recorded and approved during API review.

The API is designed API first.
The self-contained OpenAPI 3.1 contract at `contracts/openapi/marketplace-api.yaml` is the source of truth for paths, operations, inputs, outputs, authentication, and errors.
Implementation work MUST NOT begin until the proposed contract is reviewable.

## Scope

These conventions apply to synchronous REST endpoints exposed by `marketplace-service`.
Event contracts follow `contracts/asyncapi/marketplace-events.yaml`.
Provider webhook authentication and payload rules are additionally governed by `contracts/webhooks/signature-contracts.md`.

## OpenAPI Contract

The OpenAPI contract MUST:

- use OpenAPI 3.1 in a single self-contained YAML document;
- use U.S. English for names, descriptions, and examples;
- include `info.title`, `info.description`, `info.version`, `info.contact`, `info.x-api-id`, and `info.x-audience`;
- version the contract with semantic versioning;
- define every parameter, request body, response, header, and security requirement explicitly;
- include at least one success example and one representative failure example for every operation;
- keep reusable schemas, parameters, responses, and headers under `components`;
- avoid mutable remote references and repository-relative references;
- pass OpenAPI validation and the repository's configured API lint checks.

The contract version describes the specification.
It is independent of the OpenAPI format version and the deployed application version.

## Resource and URL Design

Paths MUST model business resources rather than controller methods or user interface actions.

- Use plural, domain-specific resource names such as `/products`, `/orders`, and `/access-grants`.
- Use kebab-case for path segments.
- Use URL-safe, stable resource identifiers.
- Put identifiers in path segments, for example `/products/{product_id}`.
- Use snake_case for path parameter names and query parameter names.
- Do not use a trailing slash.
- Do not create empty path segments.
- Keep paths verb-free.
- Prefer no more than two nested resource levels.
- Do not use `/api` as a base path.
- Do not put a version in the URL.

Model a business action as a resource or state transition.
For example, use `POST /checkouts` to create a checkout and `PATCH /products/{product_id}` to change publication status.
Do not create paths such as `/cart/checkout` or `/products/{product_id}/publish`.

Avoid versioning while compatible evolution is possible.
If incompatible versioning becomes unavoidable, use media type versioning and obtain consumer approval.

## HTTP Methods and Status Codes

Methods MUST retain their standard HTTP semantics.

| Method | Use | Successful response |
|---|---|---|
| `GET` | Read a resource or collection without side effects | `200 OK` |
| `POST` | Create a resource or start a non-idempotent process | `201 Created`, `200 OK`, or `202 Accepted` |
| `PUT` | Create or fully replace a resource at a client-known URI | `200 OK`, `201 Created`, or `204 No Content` |
| `PATCH` | Partially update a resource | `200 OK` or `204 No Content` |
| `DELETE` | Remove or archive the addressed resource | `204 No Content` |

A `201 Created` response MUST include a `Location` header containing the created resource URI.
A `202 Accepted` response SHOULD identify a resource through which the client can observe progress.
A `204 No Content` response MUST NOT include a response body.
`GET`, `PUT`, and `DELETE` behavior MUST be idempotent.
`GET` MUST be safe and MUST NOT produce business state changes.

Use the most specific standard status code.
The common client error mapping is:

| Status | Meaning |
|---|---|
| `400 Bad Request` | Malformed input, failed request validation, or an invalid query |
| `401 Unauthorized` | Authentication is missing or invalid |
| `403 Forbidden` | The caller is authenticated but lacks role, ownership, or tenant access |
| `404 Not Found` | The resource is absent or intentionally concealed from the caller |
| `409 Conflict` | The request conflicts with current resource state or a uniqueness rule |
| `412 Precondition Failed` | An `If-Match` or other HTTP precondition failed |
| `429 Too Many Requests` | A rate limit was exceeded |

Do not return `200 OK` with an embedded error.
Do not invent application-specific HTTP status codes.

## JSON and Data Types

Use `application/json` for normal JSON requests and responses.
Every JSON response body MUST have an object as its top-level value.
Wrap collection items in a named plural property rather than returning a bare array.

- Use snake_case for JSON property names.
- Use UPPER_SNAKE_CASE for closed enum values unless the domain standard requires another representation.
- Use `id` for the resource's identifier and `<resource>_id` for references.
- Use `created_at`, `modified_at`, and other `_at` names for instants.
- Represent instants as RFC 3339 `date-time` strings in UTC.
- Use ISO 4217 three-letter uppercase currency codes.
- Use the shared `Money` object with numeric `amount` and string `currency`.
- Do not use floating-point arithmetic for money in implementation code.
- Treat an absent optional property and an explicit `null` identically when both are allowed.
- Do not use `null` for boolean values or empty arrays.

Request and response schemas MUST state required properties and validation constraints.
The service MUST reject unknown or invalid input when accepting it could hide a client defect or security issue.
Clients MUST tolerate new response properties and unknown values in explicitly extensible fields.

## Collections, Filtering, Sorting, and Pagination

Every collection that can grow beyond a small bounded set MUST be paginated.
Cursor pagination is the default.

Use these query parameters:

- `cursor` for an opaque page cursor;
- `limit` for requested page size;
- `sort` for documented sort fields, with `-` indicating descending order;
- `fields` only when partial responses provide a measured benefit;
- domain-specific snake_case names for filters.

The default and maximum `limit` MUST be documented per operation.
The cursor MUST be opaque to clients and bound to the effective filters and ordering.
The sort order MUST be deterministic and include a unique tie-breaker.
Offset pagination requires a documented user need and API review approval.

Use this response shape:

```json
{
  "products": [],
  "pagination": {
    "self": "https://marketplace.example/products?limit=20",
    "next": "https://marketplace.example/products?limit=20&cursor=opaque"
  }
}
```

Omit `next` when no later page exists.
Do not return a total count unless a confirmed consumer use case justifies its cost and consistency semantics.
Filtering and sorting semantics, supported values, case sensitivity, and invalid-input behavior MUST be documented in OpenAPI.

## Errors

Every operation MUST be able to return RFC 9457 Problem Details with media type `application/problem+json`.
Use the standard members `type`, `title`, `status`, `detail`, and `instance`.
Marketplace extensions use snake_case.

Validation problems MAY add an `errors` array whose entries identify a stable machine-readable `code`, the invalid `field`, and a safe `detail`.

```json
{
  "type": "urn:marketplace:problem:validation-failed",
  "title": "Request validation failed",
  "status": 400,
  "detail": "One or more request fields are invalid.",
  "instance": "/problems/01J2ABCDEF",
  "errors": [
    {
      "code": "REQUIRED",
      "field": "title",
      "detail": "Title is required."
    }
  ]
}
```

Problem `type` values MUST be stable URIs using the `urn:marketplace:problem:<kebab-case-name>` namespace.
Titles and extension codes MUST remain stable enough for client handling.
Details are diagnostic text for people and MUST NOT be parsed by clients.
Responses MUST NOT expose stack traces, SQL, secrets, tokens, private file locations, or internal exception names.
Authentication and authorization failures SHOULD avoid revealing whether a protected resource exists.

## Authentication, Authorization, and Tenant Isolation

Every operation MUST explicitly declare its OpenAPI security requirement.
Public storefront reads MAY use `security: []` after review.
All other endpoints use the shared HTTP bearer security scheme.

The contract MUST state the allowed role for each protected operation.
The backend MUST enforce the role, resource ownership, and tenant boundary independently of the frontend.
Client-supplied `tenant_id`, buyer ID, merchant ID, or role MUST NOT be trusted as authorization evidence.
Tenant context and caller identity come from the authenticated principal.

Private product file access MUST verify an active access grant before returning a short-lived download capability.
Admin endpoints MUST require `ADMIN`.
Webhook endpoints MUST authenticate the provider, verify signatures, and enforce event idempotency.

## Idempotency, Concurrency, and Transactions

Each mutating operation MUST document retry behavior.
Checkout creation, payment processing, webhook ingestion, and other duplicate-sensitive `POST` operations MUST support an idempotency strategy.
Prefer a durable domain secondary key when one exists.
Otherwise use `Idempotency-Key` and persist enough information to return the original result for a safe retry.

The same key with a materially different request MUST return `409 Conflict`.
The contract MUST document idempotency key scope, retention, and replay behavior.

Use `ETag` and `If-Match` for user-visible concurrent updates when lost updates are possible.
Return `412 Precondition Failed` when the supplied representation version is stale.

Operations that require atomic state changes MUST share one service-layer transaction.
This includes checkout, webhook processing, access-grant creation, review creation, and product publication.
The API contract MUST describe observable all-or-nothing behavior without exposing persistence details.

## Caching and Observability

Every `GET` operation MUST document whether it is cacheable.
Protected or user-specific responses SHOULD default to `Cache-Control: private, no-store` unless a safe policy is designed.
Public storefront resources MAY use explicit freshness and validation policies.

The service MUST accept `X-Flow-ID` for request correlation and generate one when absent.
The response SHOULD return the effective `X-Flow-ID`.
Logs MUST correlate requests without recording credentials, payment secrets, or sensitive personal data.
Operational metrics SHOULD distinguish route templates and status classes without using unbounded identifiers as metric labels.

## Compatibility and Deprecation

Published APIs MUST remain backward compatible.
Compatible changes normally include adding optional response properties, adding optional request properties with defaults, and adding new operations.
Breaking changes include removing or renaming properties, making optional input required, changing property types or semantics, narrowing accepted input, changing authorization in a way that rejects existing clients, and changing status-code behavior clients rely on.

Do not use URL versioning to escape compatibility review.
When deprecating an operation or property, mark it `deprecated: true` in OpenAPI, identify affected consumers, agree on a migration period, and use `Deprecation` and `Sunset` headers where applicable.

## Approved Exceptions

An exception MUST be:

1. necessary for a documented marketplace use case;
2. recorded next to the affected OpenAPI operation with rationale;
3. reviewed by a backend owner and a consumer representative;
4. limited to the smallest possible scope;
5. accompanied by a compatibility and migration assessment.

Zalando-specific organization names, hostnames, OAuth scopes, and internal infrastructure references are not copied into this project.
Marketplace equivalents defined here take their place.

## Review Checklist

- [ ] The resource model is based on business resources and contains no action path.
- [ ] The path is plural, kebab-case, unversioned, and has no trailing slash.
- [ ] Query parameters and JSON properties use snake_case.
- [ ] HTTP method, safety, idempotency, and status codes are correct.
- [ ] Inputs, outputs, headers, examples, and all expected errors are explicit.
- [ ] Collection growth, deterministic ordering, and pagination are addressed.
- [ ] Authentication, role, ownership, and tenant isolation are explicit.
- [ ] Retry, duplicate, concurrency, and transaction behavior are explicit.
- [ ] Problem Details are safe and machine-readable.
- [ ] Compatibility and deprecation impact are assessed.
- [ ] The OpenAPI document validates and passes configured linting.
- [ ] A consumer representative and API reviewer approve the design.

