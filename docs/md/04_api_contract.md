# API Contract

## General API Rules

- `contracts/openapi/marketplace-api.yaml` is the API source of truth.
- Follow `contracts/convention/api-conventions.md`.
- Follow the endpoint delivery workflow in `docs/md/09_api_endpoint_delivery_process.md`.
- Use RESTful JSON APIs with plural, verb-free, unversioned resource paths.
- Do not use `/api` or a version segment as a base path for new endpoints.
- Require authentication for buyer, merchant, and admin actions.
- Enforce role, ownership, and tenant authorization in backend services, not only frontend routes.
- Return RFC 9457 Problem Details as `application/problem+json`.

The endpoint tables below are the initial MVP capability inventory.
Their paths predate the project convention and are design candidates rather than an implementation contract.
Each path MUST be normalized in the OpenAPI-first review before it is implemented.

## Error Response Format

```json
{
  "type": "urn:marketplace:problem:forbidden",
  "title": "Access denied",
  "status": 403,
  "detail": "You do not have access to this resource.",
  "instance": "/problems/01J2ABCDEF"
}
```

## Auth APIs

| Method | Path | Access | Purpose |
|---|---|---|---|
| POST | `/api/v1/auth/signup` | Public | Register user |
| POST | `/api/v1/auth/login` | Public | Sign in |
| POST | `/api/v1/auth/logout` | Authenticated | Sign out |
| GET | `/api/v1/auth/me` | Authenticated | Current user |

## Store APIs

| Method | Path | Access | Purpose |
|---|---|---|---|
| POST | `/api/v1/stores` | MERCHANT | Create store |
| GET | `/api/v1/stores/{storeSlug}` | Public | Get public store |
| PATCH | `/api/v1/merchant/stores/{storeId}` | MERCHANT | Update own store |
| GET | `/api/v1/merchant/stores/current` | MERCHANT | Get current merchant store |

## Product APIs

| Method | Path | Access | Purpose |
|---|---|---|---|
| GET | `/api/v1/products` | Public | Search products |
| GET | `/api/v1/products/{productId}` | Public | Get product detail |
| POST | `/api/v1/merchant/products` | MERCHANT | Create product |
| PATCH | `/api/v1/merchant/products/{productId}` | MERCHANT | Update own product |
| DELETE | `/api/v1/merchant/products/{productId}` | MERCHANT | Archive or remove own product |
| POST | `/api/v1/merchant/products/{productId}/publish` | MERCHANT | Publish product |
| POST | `/api/v1/merchant/products/{productId}/unpublish` | MERCHANT | Unpublish product |

## Product Media APIs

| Method | Path | Access | Purpose |
|---|---|---|---|
| POST | `/api/v1/merchant/products/{productId}/images` | MERCHANT | Upload product image |
| POST | `/api/v1/merchant/products/{productId}/files` | MERCHANT | Upload private file |
| GET | `/api/v1/library/products/{productId}/download` | BUYER | Generate secure download URL |

## Cart APIs

| Method | Path | Access | Purpose |
|---|---|---|---|
| GET | `/api/v1/cart` | BUYER | Get active cart |
| POST | `/api/v1/cart/items` | BUYER | Add product to cart |
| DELETE | `/api/v1/cart/items/{itemId}` | BUYER | Remove cart item |
| POST | `/api/v1/cart/checkout` | BUYER | Create checkout session |

## Order and Payment APIs

| Method | Path | Access | Purpose |
|---|---|---|---|
| GET | `/api/v1/orders` | BUYER | Get buyer payment history |
| GET | `/api/v1/orders/{orderId}` | BUYER | Get order detail |
| POST | `/api/v1/payments/webhook/stripe` | Payment provider | Handle Stripe webhook |
| POST | `/api/v1/payments/mock/success/{orderId}` | Dev only | Simulate successful payment |
| POST | `/api/v1/payments/mock/failure/{orderId}` | Dev only | Simulate failed payment |
| GET | `/api/v1/merchant/orders` | MERCHANT | Get merchant order view |

## Library APIs

| Method | Path | Access | Purpose |
|---|---|---|---|
| GET | `/api/v1/library` | BUYER | Get purchased products |
| GET | `/api/v1/library/products/{productId}` | BUYER | Get purchased product detail |

## Review APIs

| Method | Path | Access | Purpose |
|---|---|---|---|
| GET | `/api/v1/products/{productId}/reviews` | Public | List reviews |
| POST | `/api/v1/products/{productId}/reviews` | BUYER | Create review |
| PATCH | `/api/v1/reviews/{reviewId}` | BUYER | Update own review |
| DELETE | `/api/v1/reviews/{reviewId}` | BUYER | Delete own review |

## Admin APIs

| Method | Path | Access | Purpose |
|---|---|---|---|
| GET | `/api/v1/admin/users` | ADMIN | List users |
| PATCH | `/api/v1/admin/users/{userId}` | ADMIN | Update user status |
| GET | `/api/v1/admin/stores` | ADMIN | List stores |
| PATCH | `/api/v1/admin/stores/{storeId}` | ADMIN | Update store status |
| GET | `/api/v1/admin/categories` | ADMIN | List categories |
| POST | `/api/v1/admin/categories` | ADMIN | Create category |
| PATCH | `/api/v1/admin/categories/{categoryId}` | ADMIN | Update category |
| PATCH | `/api/v1/admin/products/{productId}/visibility` | ADMIN | Moderate product visibility |
| PATCH | `/api/v1/admin/reviews/{reviewId}/visibility` | ADMIN | Moderate review visibility |

## Required Validation Rules

| Operation | Validation |
|---|---|
| Create store | Unique slug, authenticated merchant role |
| Create product | Store ownership, required fields |
| Publish product | Title, description, image, file, price, category, stock |
| Add to cart | Product is published, store is active, stock available |
| Checkout | Cart not empty, products still available |
| Webhook | Valid signature or mock mode, unique event ID |
| Download file | Active access grant exists |
| Create review | Paid product, one review per buyer per product, rating 1 to 5 |
| Admin action | ADMIN role required |
