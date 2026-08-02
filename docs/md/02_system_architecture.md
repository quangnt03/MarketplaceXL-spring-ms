# System Architecture

## Architecture Overview

The system uses a three-tier web architecture with a minimal service split.

```mermaid
flowchart TD
    User[Buyer Merchant Admin] --> FE[Next.js Frontend]

    FE --> GW[Ingress or API Gateway]

    GW --> MS[marketplace-service modular monolith]

    MS --> DB[(Oracle Database)]

    MS --> Redis[(Redis)]

    MS --> MQ[(RabbitMQ)]

    MS --> Storage[(LocalStack S3 or AWS S3)]
    MS --> Stripe[Stripe or Payment Mock]

    Stripe --> MS
```

## Services

### marketplace-service

Primary service for core business operations.

Responsibilities:

- User role lookup
- Store management
- Tenant isolation
- Product management
- Category management
- Storefront product browsing
- Search and filtering
- Cart management
- Order creation
- Buyer library
- Review management
- Admin management
- Secure download URL generation
- Payment session creation
- Platform fee calculation
- Payment status tracking
- Payment webhook verification
- Idempotent webhook processing
- Publish internal application events
- Consume RabbitMQ events
- Create in-app notifications
- Optional email notification later

The service is a Spring Modulith modular monolith.
Each marketplace domain is a top-level application module under `com.example.marketplace`.
Modules may depend only on explicitly published named interfaces and application events.
The platform module exposes the `platform::api` extension contract used when adding a new module.

## Shared Infrastructure

| Component | Purpose |
|---|---|
| Oracle Database | Source of truth for users, stores, products, orders, payments, reviews, and access grants. |
| Redis | Cache public storefront, category, product detail, and rate limiting counters. |
| RabbitMQ | AMQP-based async event delivery. |
| LocalStack S3 or AWS S3 | Store product images and private digital files. |
| GitHub Actions | CI pipeline. |
| Docker Compose | Local runtime. |
| Kubernetes or Minikube | Later deployment validation. |

## Communication Patterns

### Synchronous Communication

Use REST over HTTP for frontend to backend communication.

Examples:

- Search products
- Create product
- Add item to cart
- Create checkout session
- View library
- Submit review

### Asynchronous Communication

Use RabbitMQ for background side effects.

Events:

| Event | Producer | Consumer |
|---|---|---|
| payment.succeeded | payment module | orders, library, notification handlers |
| payment.failed | payment module | orders, notification handlers |
| product.published | catalog module | storefront, notification handlers |
| review.created | review module | catalog |
| order.completed | orders module | library, notification handlers |

## Tenant Isolation Strategy

Use shared Oracle database and shared schema with `tenant_id` on tenant-owned tables.

Tenant-owned tables include:

- stores
- merchant_memberships
- products
- product_media
- product_files
- order_items
- access_grants
- reviews

Rules:

- Merchant-facing queries must filter by tenant ID.
- Merchant product updates must check tenant ownership.
- Admin APIs may perform cross-tenant operations only with ADMIN role.

## Caching Strategy

Use Redis only as a cache, not as source of truth.

Cache keys:

| Key Pattern | Data |
|---|---|
| `store:{storeSlug}` | Store profile |
| `store:{storeSlug}:products:{hash}` | Store product listing |
| `product:{productId}` | Product detail |
| `categories:active` | Active category list |
| `search:{hash}` | Search results |

Invalidate cache on:

- Product created
- Product updated
- Product published
- Product unpublished
- Product suspended
- Review created
- Category updated

## Deployment View

Local MVP:

```text
Next.js frontend
Spring Boot marketplace-service
Oracle container
Redis container
RabbitMQ container
LocalStack S3 container
```

Kubernetes later:

```text
Deployment
Service
Ingress
ConfigMap
Secret
PersistentVolumeClaim for local object storage or Oracle development only
```
