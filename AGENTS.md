# Codex Implementation Rules

## Primary Instruction

This repository is the source of truth for the Marketplace Platform MVP. Implement only the marketplace MVP described here and in `docs/md`. Do not add features outside scope without explicit instruction.

Project-scope naming is `marketplace`, not `erp`. Use:

- Maven parent artifact: `marketplace-platform`
- Backend module: `backend/marketplace-service`
- Java base package: `com.example.marketplace`
- Spring application class: `MarketplaceApplication`
- Frontend package: `marketplace-web`
- Contract files: `marketplace-api.yaml` and `marketplace-events.yaml`

Do not rename the project, packages, modules, or docs back to ERP naming.

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

Prioritize these tests:

1. Tenant isolation tests
2. RBAC tests
3. Checkout transaction tests
4. Webhook idempotency tests
5. Private file access tests
6. Review eligibility tests

## Done Definition

A feature is done only when:

1. Backend API works.
2. Authorization is enforced.
3. Database migration exists.
4. Core tests exist.
5. Frontend can call the API if the feature is user-facing.
6. Error cases are handled.
7. No out-of-scope feature was added.
