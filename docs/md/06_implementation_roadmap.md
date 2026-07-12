# Implementation Roadmap

## Milestone 1: Foundation

Goal: Create runnable local project foundation.

Deliverables:

- Next.js frontend project
- Spring Boot backend project or services
- Oracle database container
- Redis container
- RabbitMQ container
- MinIO container
- Docker Compose setup
- Basic GitHub Actions workflow
- Health check endpoint

Completion criteria:

- Frontend starts locally.
- Backend starts locally.
- Backend connects to Oracle.
- Backend health endpoint works.
- CI runs tests and build.

## Milestone 2: Auth, RBAC, and Tenant Store

Goal: Establish identity, roles, and tenant isolation.

Deliverables:

- Sign up
- Sign in
- Sign out
- BUYER, MERCHANT, ADMIN roles
- Store creation
- Merchant membership
- Tenant-scoped merchant dashboard
- Authorization tests

Completion criteria:

- Merchant can create a store.
- Buyer cannot access merchant APIs.
- Merchant A cannot access Merchant B store data.
- Admin can access admin-only route.

## Milestone 3: Product and Storefront

Goal: Allow merchant to publish products and buyers to view them.

Deliverables:

- Category CRUD for admin
- Product create and update
- Product image upload
- Private file upload
- Publish and unpublish controls
- Public storefront page
- Product detail page

Completion criteria:

- Merchant can publish complete product.
- Public storefront shows only published products.
- Product detail shows price, stock, category, rating summary.

## Milestone 4: Search, Cart, and Checkout

Goal: Implement product discovery and purchase intent.

Deliverables:

- Keyword search
- Category filter
- Store filter
- Price sort
- Discount sort
- Trend sort placeholder
- Cart add and remove
- Checkout session creation
- Platform fee calculation
- Pending order creation

Completion criteria:

- Buyer can add published product to cart.
- Buyer can checkout valid cart.
- System creates pending order and pending payment.

## Milestone 5: Payment and Buyer Library

Goal: Complete payment confirmation and access grant.

Deliverables:

- Stripe test mode or mock payment adapter
- Payment success handling
- Payment failure handling
- Payment cancellation handling
- Webhook event idempotency
- Access grant creation
- Buyer payment history
- Buyer library
- Secure download URL generation

Completion criteria:

- Successful payment creates access grant.
- Failed payment does not create access grant.
- Duplicate webhook does not duplicate access grant.
- Buyer can download only purchased product.

## Milestone 6: Reviews and Admin Dashboard

Goal: Add marketplace trust and platform control.

Deliverables:

- Review creation
- One review per purchased product
- Average rating update
- Review count update
- User management
- Store management
- Product visibility moderation
- Category management
- Review moderation

Completion criteria:

- Buyer can review purchased product.
- Buyer cannot review unpurchased product.
- Buyer cannot review same product twice.
- Admin can hide review and suspend product.

## Milestone 7: Production Readiness

Goal: Improve quality and deployability.

Deliverables:

- Redis caching
- Cache invalidation
- Audit logs
- Integration tests
- API tests
- Tenant isolation tests
- Docker image build
- Kubernetes manifests or Minikube setup

Completion criteria:

- Core tests pass in CI.
- Docker Compose runs full stack.
- Kubernetes manifests are valid.
- Application can be demoed from local or Minikube environment.

## Suggested Build Order

1. Backend schema and migrations
2. Auth and RBAC
3. Tenant store
4. Product management
5. Storefront
6. Cart
7. Checkout
8. Payment webhook
9. Access grant and library
10. Reviews
11. Admin dashboard
12. Caching
13. CI/CD and deployment polish
