# Testing Plan

## Testing Priorities

Focus testing on correctness risks:

1. Tenant isolation
2. RBAC
3. Checkout transaction consistency
4. Payment webhook idempotency
5. Secure file access
6. Review eligibility
7. Search result visibility

## Unit Tests

| Test Case | Expected Result |
|---|---|
| Platform fee calculation | Correct fee amount returned |
| Product publish validation missing title | Validation fails |
| Product publish validation missing file | Validation fails |
| Rating below 1 | Validation fails |
| Rating above 5 | Validation fails |
| Buyer role permission check | Buyer cannot access merchant operation |
| Merchant role permission check | Merchant can access own store operation |
| Admin role permission check | Admin can access admin operation |
| Order state transition PENDING to PAID | Allowed |
| Order state transition FAILED to PAID | Rejected unless explicitly allowed |

## Integration Tests

| Test Case | Expected Result |
|---|---|
| Create store | Store and merchant membership created |
| Create product under store | Product contains correct tenant ID |
| Merchant A edits Merchant B product | 403 Forbidden |
| Search products | Only published products from active stores returned |
| Add unpublished product to cart | Request rejected |
| Create checkout from valid cart | Pending order and payment created |
| Process payment success webhook | Order paid and access grant created |
| Process payment failure webhook | Order failed and no access grant created |
| Process duplicate webhook | No duplicate access grant created |
| Download purchased product | Signed URL returned |
| Download unpurchased product | 403 Forbidden |
| Create review after purchase | Review created and aggregate updated |
| Create review without purchase | 403 Forbidden |
| Create duplicate review | Conflict returned |

## API Tests

| Endpoint | Scenario | Expected Result |
|---|---|---|
| GET `/api/v1/products` | Public search | Published products returned |
| POST `/api/v1/merchant/products` | Merchant creates product | Product created |
| PATCH `/api/v1/merchant/products/{id}` | Merchant updates own product | Product updated |
| PATCH `/api/v1/merchant/products/{id}` | Merchant updates another tenant product | 403 Forbidden |
| POST `/api/v1/cart/items` | Buyer adds published product | Item added |
| POST `/api/v1/cart/checkout` | Buyer checks out cart | Checkout URL returned |
| POST `/api/v1/payments/webhook/stripe` | Valid payment success | Order paid |
| GET `/api/v1/library` | Buyer requests library | Own purchased products returned |
| POST `/api/v1/products/{id}/reviews` | Verified buyer reviews | Review created |
| GET `/api/v1/admin/users` | Buyer requests users | 403 Forbidden |

## End-to-End Tests

### E2E 1: Merchant Publishes Product

Steps:

1. Merchant signs in.
2. Merchant creates store.
3. Merchant creates product.
4. Merchant uploads image.
5. Merchant uploads private file.
6. Merchant publishes product.
7. Buyer opens storefront.

Expected result:

- Product appears on storefront.
- Product appears in search.
- Private file is not public.

### E2E 2: Buyer Purchases Product

Steps:

1. Buyer signs in.
2. Buyer searches product.
3. Buyer opens product detail.
4. Buyer adds product to cart.
5. Buyer starts checkout.
6. Payment success event is processed.
7. Buyer opens library.
8. Buyer downloads product.

Expected result:

- Order is marked PAID.
- Access grant exists.
- Buyer can download private file.

### E2E 3: Payment Failure

Steps:

1. Buyer starts checkout.
2. Payment fails or is cancelled.
3. Buyer opens library.

Expected result:

- Order is FAILED or CANCELLED.
- No access grant exists.
- Product is not available in library.

## Security Tests

| Area | Test | Expected Result |
|---|---|---|
| Auth | Unauthenticated user opens merchant dashboard | Redirect or 401 |
| RBAC | Buyer calls admin API | 403 Forbidden |
| Tenant isolation | Merchant modifies another merchant product | 403 Forbidden |
| Private file | Buyer guesses file URL | Access denied |
| Webhook | Invalid signature | Request rejected |
| Review | Non-purchaser creates review | 403 Forbidden |

## CI Requirements

Run on every pull request:

1. Backend unit tests
2. Backend integration tests
3. Frontend build
4. Docker image build
5. API smoke tests where feasible
6. Static checks
