# Transactions and State Design

## State Machines

### Product State

```text
DRAFT -> PUBLISHED
PUBLISHED -> UNPUBLISHED
UNPUBLISHED -> PUBLISHED
PUBLISHED -> SUSPENDED
SUSPENDED -> UNPUBLISHED
```

Rules:

- Only merchant can publish or unpublish own product.
- Only admin can suspend product.
- Public storefront only shows PUBLISHED products from ACTIVE stores.

### Order State

```text
PENDING -> PAID
PENDING -> FAILED
PENDING -> CANCELLED
PENDING -> EXPIRED
PAID -> REFUNDED
```

Rules:

- Access is granted only after order becomes PAID.
- FAILED, CANCELLED, and EXPIRED orders do not grant access.

### Payment State

```text
PENDING -> SUCCEEDED
PENDING -> FAILED
PENDING -> CANCELLED
SUCCEEDED -> REFUNDED
```

Rules:

- Payment state changes are driven by payment provider events or mock payment events.
- Duplicate events must not duplicate side effects.

### Access Grant State

```text
ACTIVE -> REVOKED
```

Rules:

- ACTIVE access grant allows file download.
- REVOKED access grant blocks download.

## Transaction Boundaries

### Store Creation Transaction

Operations:

1. Create store.
2. Create merchant membership.
3. Assign MERCHANT role if missing.
4. Write audit log.

Rollback if any step fails.

### Product Publish Transaction

Operations:

1. Check merchant owns product tenant.
2. Validate product completeness.
3. Update product status to PUBLISHED.
4. Invalidate cache.
5. Write audit log.

### Checkout Creation Transaction

Operations:

1. Load active buyer cart.
2. Validate cart is not empty.
3. Validate each product is published.
4. Validate each store is active.
5. Validate stock is available.
6. Capture current price into order items.
7. Create PENDING order.
8. Create order items.
9. Calculate subtotal.
10. Calculate platform fee.
11. Create PENDING payment.
12. Mark cart as checkout started or checked out after provider session is created.

Important:

- Do not grant access during checkout creation.
- Access is granted only from payment success processing.

### Payment Webhook Transaction

Operations:

1. Verify provider signature or mock mode.
2. Insert webhook event ID into `webhook_events`.
3. If event already exists, return success without side effects.
4. Load payment by provider session ID.
5. Load order.
6. Update payment status.
7. Update order status.
8. If payment succeeded, create access grants for order items.
9. Publish async events to RabbitMQ.
10. Write audit log.

Required uniqueness:

- `webhook_events(provider, event_id)` is unique.
- `access_grants(buyer_user_id, product_id)` is unique.

### Review Creation Transaction

Operations:

1. Check buyer has active access grant or paid order item.
2. Check buyer has not reviewed product before.
3. Create review.
4. Recalculate product average rating.
5. Recalculate product review count.
6. Update product aggregate fields.
7. Publish review event.

Required uniqueness:

- `reviews(buyer_user_id, product_id)` is unique.

## Race Condition Prevention

### Duplicate Checkout

Risk:

- Buyer submits checkout twice.

Mitigation:

- Check cart status.
- Use database transaction.
- Mark cart as checked out or checkout in progress.

### Duplicate Webhook

Risk:

- Payment provider sends same event multiple times.

Mitigation:

- Unique constraint on provider event ID.
- Return success for already processed event.

### Duplicate Access Grant

Risk:

- Same paid order event creates duplicate product access.

Mitigation:

- Unique constraint on buyer and product in access grants.

### Duplicate Review

Risk:

- Buyer submits two review requests concurrently.

Mitigation:

- Unique constraint on buyer and product in reviews.

### Stock Race Condition

Risk:

- Two buyers purchase last available stock.

Mitigation:

- Use transactional stock validation.
- Use conditional update where stock is greater than or equal to requested quantity.

## Pseudocode

### processPaymentSucceeded

```text
function processPaymentSucceeded(event):
    verifyEvent(event)

    begin transaction

    if webhookEventExists(event.provider, event.eventId):
        commit
        return success

    insertWebhookEvent(event.provider, event.eventId, event.type)

    payment = findPaymentBySessionId(event.sessionId)
    if payment is null:
        markWebhookFailed(event)
        commit
        return success

    order = findOrder(payment.orderId)

    if order.status == PAID:
        markWebhookProcessed(event)
        commit
        return success

    update payment status to SUCCEEDED
    update order status to PAID

    for item in order.items:
        create access grant if not exists

    publish payment.succeeded event
    create audit log
    mark webhook processed

    commit
    return success
```

### createReview

```text
function createReview(buyerId, productId, rating, comment):
    if rating < 1 or rating > 5:
        return validation error

    begin transaction

    if not hasActiveAccessGrant(buyerId, productId):
        rollback
        return forbidden

    if reviewExists(buyerId, productId):
        rollback
        return conflict

    create review
    recompute average rating and review count
    update product aggregate
    publish review.created event

    commit
    return review
```
