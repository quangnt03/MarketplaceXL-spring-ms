# Project Brief

## Product Name

Multi-Tenant Digital Product Marketplace

## One-Line Summary

A multi-tenant marketplace where merchants create storefronts, publish digital products, buyers purchase products, and the system grants secure access after confirmed payment.

## Product Goal

Build a bounded, production-minded marketplace core that demonstrates backend correctness, tenant isolation, role-based access control, transactional checkout, payment webhook handling, secure product access, search, caching, and admin operations.

## Target Users

| User Type | Description                                                                                 |
| --------- | ------------------------------------------------------------------------------------------- |
| Buyer     | Browses products, purchases digital products, accesses purchased products, writes reviews.  |
| Merchant  | Creates a store, manages products, publishes or unpublishes products, views own store data. |
| Admin     | Manages platform-level users, stores, categories, products, reviews, and configuration.     |

## Core Business Flow

1. Merchant signs up.
2. Merchant creates a tenant store.
3. Merchant creates and publishes a digital product.
4. Buyer browses storefront or searches products.
5. Buyer adds product to cart.
6. Buyer starts checkout.
7. Payment provider confirms payment through webhook.
8. System marks order as paid.
9. System grants buyer access to purchased product.
10. Buyer sees product in library and can review it.

## Technical Stack

| Layer           | Technology                                  |
| --------------- | ------------------------------------------- |
| Frontend        | Next.js, TypeScript                         |
| Backend         | Spring Boot, Java                           |
| Database        | Oracle Database                             |
| ORM             | Hibernate, Spring Data JPA                  |
| Auth            | Spring Security, OAuth2 or JWT-based auth   |
| Cache           | Redis                                       |
| Async Messaging | RabbitMQ using AMQP                         |
| Object Storage  | MinIO locally, S3-compatible storage later  |
| Payment         | Stripe test mode or mock payment adapter    |
| Local Runtime   | Docker Compose                              |
| Orchestration   | Minikube or Kubernetes after MVP core works |
| CI/CD           | GitHub Actions                              |

## Architectural Direction

| Service              | Responsibility                                                      |
| -------------------- | ------------------------------------------------------------------- |
| marketplace-service  | Stores, products, cart, orders, library, reviews, admin APIs.       |
| payment-service      | Payment session creation, webhook processing, payment state update. |
| notification-service | In-app notifications and async event consumers.                     |


## Non-Goals

The MVP is not intended to become a full Gumroad, Shopify, or enterprise marketplace replacement.

The MVP does not solve:

- Tax calculation
- Refund automation
- Chargeback handling
- Fraud detection
- Real merchant payout reconciliation
- Full Stripe Connect production onboarding
- Subscription products
- Physical product shipping
- Multi-region high availability
- Advanced analytics
