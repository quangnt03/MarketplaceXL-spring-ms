# Data Model

## Database

Use Oracle Database as the primary source of truth.

## ID Strategy

Use UUIDs for business entities.

Acceptable Oracle storage options:

- `RAW(16)` for compact UUID storage
- `VARCHAR2(36)` for simpler debugging

For implementation speed, `VARCHAR2(36)` is acceptable for MVP.

## Core Tables

### users

| Column | Type | Notes |
|---|---|---|
| id | VARCHAR2(36) | Primary key |
| email | VARCHAR2(255) | Unique |
| password_hash | VARCHAR2(255) | Nullable if OAuth-only |
| display_name | VARCHAR2(255) | User name |
| status | VARCHAR2(30) | ACTIVE, DISABLED |
| created_at | TIMESTAMP | Created time |
| updated_at | TIMESTAMP | Updated time |

### roles

| Column | Type | Notes |
|---|---|---|
| id | VARCHAR2(36) | Primary key |
| name | VARCHAR2(30) | BUYER, MERCHANT, ADMIN |

### user_roles

| Column | Type | Notes |
|---|---|---|
| user_id | VARCHAR2(36) | FK users.id |
| role_id | VARCHAR2(36) | FK roles.id |

Unique constraint: `(user_id, role_id)`.

### stores

| Column | Type | Notes |
|---|---|---|
| id | VARCHAR2(36) | Primary key |
| owner_user_id | VARCHAR2(36) | FK users.id |
| name | VARCHAR2(255) | Store name |
| slug | VARCHAR2(255) | Unique store slug |
| subdomain | VARCHAR2(255) | Unique, nullable |
| description | CLOB | Store description |
| status | VARCHAR2(30) | ACTIVE, SUSPENDED, DISABLED |
| created_at | TIMESTAMP | Created time |
| updated_at | TIMESTAMP | Updated time |

### merchant_memberships

| Column | Type | Notes |
|---|---|---|
| id | VARCHAR2(36) | Primary key |
| tenant_id | VARCHAR2(36) | FK stores.id |
| user_id | VARCHAR2(36) | FK users.id |
| merchant_role | VARCHAR2(30) | OWNER, MANAGER |
| created_at | TIMESTAMP | Created time |

Unique constraint: `(tenant_id, user_id)`.

### categories

| Column | Type | Notes |
|---|---|---|
| id | VARCHAR2(36) | Primary key |
| name | VARCHAR2(255) | Category name |
| slug | VARCHAR2(255) | Unique slug |
| status | VARCHAR2(30) | ACTIVE, HIDDEN |
| created_at | TIMESTAMP | Created time |

### products

| Column | Type | Notes |
|---|---|---|
| id | VARCHAR2(36) | Primary key |
| tenant_id | VARCHAR2(36) | FK stores.id |
| category_id | VARCHAR2(36) | FK categories.id |
| title | VARCHAR2(255) | Product title |
| slug | VARCHAR2(255) | Product slug |
| description | CLOB | Product description |
| price_amount | NUMBER(12,2) | Product price |
| discount_amount | NUMBER(12,2) | Nullable discount amount |
| currency | VARCHAR2(10) | Example: USD |
| stock_quantity | NUMBER(10) | Stock or license count |
| status | VARCHAR2(30) | DRAFT, PUBLISHED, UNPUBLISHED, SUSPENDED |
| average_rating | NUMBER(3,2) | Cached rating |
| review_count | NUMBER(10) | Cached review count |
| created_by | VARCHAR2(36) | FK users.id |
| created_at | TIMESTAMP | Created time |
| updated_at | TIMESTAMP | Updated time |

Unique constraint: `(tenant_id, slug)`.

### product_media

| Column | Type | Notes |
|---|---|---|
| id | VARCHAR2(36) | Primary key |
| tenant_id | VARCHAR2(36) | FK stores.id |
| product_id | VARCHAR2(36) | FK products.id |
| media_type | VARCHAR2(30) | IMAGE |
| object_key | VARCHAR2(500) | Storage key |
| public_url | VARCHAR2(1000) | Nullable public URL |
| display_order | NUMBER(10) | Sort order |
| created_at | TIMESTAMP | Created time |

### product_files

| Column | Type | Notes |
|---|---|---|
| id | VARCHAR2(36) | Primary key |
| tenant_id | VARCHAR2(36) | FK stores.id |
| product_id | VARCHAR2(36) | FK products.id |
| file_name | VARCHAR2(255) | Original file name |
| object_key | VARCHAR2(500) | Private storage key |
| file_size | NUMBER(19) | File size |
| content_type | VARCHAR2(255) | MIME type |
| status | VARCHAR2(30) | ACTIVE, REPLACED, REMOVED |
| created_at | TIMESTAMP | Created time |

### carts

| Column | Type | Notes |
|---|---|---|
| id | VARCHAR2(36) | Primary key |
| buyer_user_id | VARCHAR2(36) | FK users.id |
| status | VARCHAR2(30) | ACTIVE, CHECKED_OUT, ABANDONED |
| created_at | TIMESTAMP | Created time |
| updated_at | TIMESTAMP | Updated time |

### cart_items

| Column | Type | Notes |
|---|---|---|
| id | VARCHAR2(36) | Primary key |
| cart_id | VARCHAR2(36) | FK carts.id |
| product_id | VARCHAR2(36) | FK products.id |
| quantity | NUMBER(10) | Usually 1 for digital products |
| unit_price_amount | NUMBER(12,2) | Captured price |
| created_at | TIMESTAMP | Created time |

Unique constraint: `(cart_id, product_id)`.

### orders

| Column | Type | Notes |
|---|---|---|
| id | VARCHAR2(36) | Primary key |
| buyer_user_id | VARCHAR2(36) | FK users.id |
| status | VARCHAR2(30) | PENDING, PAID, FAILED, CANCELLED, EXPIRED, REFUNDED |
| subtotal_amount | NUMBER(12,2) | Subtotal |
| platform_fee_amount | NUMBER(12,2) | Platform fee |
| total_amount | NUMBER(12,2) | Total amount |
| currency | VARCHAR2(10) | Currency |
| payment_provider | VARCHAR2(50) | STRIPE or MOCK |
| payment_session_id | VARCHAR2(255) | Provider session ID |
| created_at | TIMESTAMP | Created time |
| updated_at | TIMESTAMP | Updated time |

### order_items

| Column | Type | Notes |
|---|---|---|
| id | VARCHAR2(36) | Primary key |
| order_id | VARCHAR2(36) | FK orders.id |
| tenant_id | VARCHAR2(36) | FK stores.id |
| product_id | VARCHAR2(36) | FK products.id |
| product_title_snapshot | VARCHAR2(255) | Title at purchase time |
| merchant_name_snapshot | VARCHAR2(255) | Store name at purchase time |
| quantity | NUMBER(10) | Quantity |
| unit_price_amount | NUMBER(12,2) | Captured price |
| total_amount | NUMBER(12,2) | Line total |

### payments

| Column | Type | Notes |
|---|---|---|
| id | VARCHAR2(36) | Primary key |
| order_id | VARCHAR2(36) | FK orders.id |
| provider | VARCHAR2(50) | STRIPE or MOCK |
| provider_session_id | VARCHAR2(255) | Checkout session ID |
| provider_payment_id | VARCHAR2(255) | Payment intent ID, nullable |
| status | VARCHAR2(30) | PENDING, SUCCEEDED, FAILED, CANCELLED, REFUNDED |
| amount | NUMBER(12,2) | Amount |
| currency | VARCHAR2(10) | Currency |
| latest_event_id | VARCHAR2(255) | Latest provider event |
| created_at | TIMESTAMP | Created time |
| updated_at | TIMESTAMP | Updated time |

### webhook_events

| Column | Type | Notes |
|---|---|---|
| id | VARCHAR2(36) | Primary key |
| provider | VARCHAR2(50) | STRIPE or MOCK |
| event_id | VARCHAR2(255) | Provider event ID |
| event_type | VARCHAR2(255) | Provider event type |
| status | VARCHAR2(30) | PROCESSED, FAILED |
| payload_hash | VARCHAR2(255) | Optional |
| processed_at | TIMESTAMP | Processed time |

Unique constraint: `(provider, event_id)`.

### access_grants

| Column | Type | Notes |
|---|---|---|
| id | VARCHAR2(36) | Primary key |
| buyer_user_id | VARCHAR2(36) | FK users.id |
| tenant_id | VARCHAR2(36) | FK stores.id |
| product_id | VARCHAR2(36) | FK products.id |
| order_item_id | VARCHAR2(36) | FK order_items.id |
| status | VARCHAR2(30) | ACTIVE, REVOKED |
| granted_at | TIMESTAMP | Grant time |

Unique constraint: `(buyer_user_id, product_id)`.

### reviews

| Column | Type | Notes |
|---|---|---|
| id | VARCHAR2(36) | Primary key |
| tenant_id | VARCHAR2(36) | FK stores.id |
| product_id | VARCHAR2(36) | FK products.id |
| buyer_user_id | VARCHAR2(36) | FK users.id |
| rating | NUMBER(1) | 1 to 5 |
| comment | CLOB | Written review |
| status | VARCHAR2(30) | VISIBLE, HIDDEN, REMOVED |
| created_at | TIMESTAMP | Created time |
| updated_at | TIMESTAMP | Updated time |

Unique constraint: `(buyer_user_id, product_id)`.

### notifications

| Column | Type | Notes |
|---|---|---|
| id | VARCHAR2(36) | Primary key |
| user_id | VARCHAR2(36) | FK users.id |
| type | VARCHAR2(100) | Notification type |
| title | VARCHAR2(255) | Title |
| message | CLOB | Message |
| read_at | TIMESTAMP | Nullable |
| created_at | TIMESTAMP | Created time |

### audit_logs

| Column | Type | Notes |
|---|---|---|
| id | VARCHAR2(36) | Primary key |
| actor_user_id | VARCHAR2(36) | FK users.id |
| tenant_id | VARCHAR2(36) | Nullable store ID |
| action | VARCHAR2(100) | Action name |
| resource_type | VARCHAR2(100) | Resource type |
| resource_id | VARCHAR2(36) | Resource ID |
| metadata_json | CLOB | JSON string |
| created_at | TIMESTAMP | Created time |
