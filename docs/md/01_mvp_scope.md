# MVP Scope

## Purpose

This document defines the MVP feature boundary for the multi-tenant digital product marketplace. It is the baseline for use case diagrams, architecture diagrams, ERD, API conventions, state models, testing strategy, and implementation planning.

## MVP Success Definition

The MVP is complete when the system supports this end-to-end flow:

1. Merchant signs up, signs in, and creates a tenant store.
2. Merchant adds a contributor with store-scoped permissions.
3. Merchant or contributor creates a digital product draft with title, description, image, price, category, stock, and private file.
4. Merchant or contributor publishes the completed product.
5. Buyer discovers the product through a public storefront or product search.
6. Buyer adds the product to cart and checks out.
7. Payment success, failure, or cancellation is processed through a payment provider or mock adapter.
8. Successful payment marks the order as paid and grants buyer access to the purchased product file.
9. Buyer can view purchase history and product library.
10. Buyer can submit one review for the purchased product.
11. Admin can manage users, merchants, stores, categories, product visibility, marketplace settings, and review moderation.

## MVP Baseline Deliverables

### Requirements and Design Baseline

The following artifacts must be frozen before broad implementation begins:

- MVP use case diagrams
- MVP feature scope
- MVP excluded feature list
- High-level architecture diagram
- AWS deployment diagram
- Initial ERD
- API response and error convention
- State models
- Testing strategy

### Technical Foundation

The following foundation work is in scope for the MVP:

- Initialize backend project.
- Initialize frontend project.
- Configure database migration.
- Configure environment variables.
- Configure local development stack.
- Configure logging.
- Configure base CI workflow.
- Configure deployment baseline.

## Product Scope

### 1. Identity, Authentication, and Authorization

Authentication is required because the MVP includes sign up, sign in, sign out, anonymous storefront browsing, authenticated role-specific actions, ownership checks, and protected routes.

#### User Account Management

In scope:

- Sign up
- Sign in
- Sign out
- View current user profile
- Update own profile
- Disable blocked user login

Acceptance criteria:

- Users can create an account.
- Users can sign in and sign out.
- Authenticated users can view their current profile.
- Authenticated users can update their own profile.
- Disabled users cannot sign in.
- Anonymous users can browse public storefronts and product details.
- Checkout requires authentication.

#### Role Model

Supported roles:

- Buyer
- Merchant
- Contributor
- Admin

Acceptance criteria:

- Each protected API validates the current user's role.
- Users can be assigned one or more supported roles according to business rules.
- Buyer actions are limited to browsing, cart, checkout, library, purchase history, and reviews.
- Merchant actions are limited to owned stores and owned products.
- Contributor actions are limited to assigned stores and assigned permissions.
- Admin actions can operate on platform-level resources.

#### Authorization Guards

In scope:

- Protect authenticated routes.
- Protect buyer actions.
- Protect merchant dashboard actions.
- Protect contributor actions.
- Protect admin actions.
- Reject cross-store access.
- Reject disabled user access.

Acceptance criteria:

- Protected frontend routes reject unauthenticated users.
- Backend APIs enforce authorization independently of frontend route protection.
- Buyers cannot access merchant dashboards.
- Buyers cannot access admin dashboards.
- Merchants cannot access admin dashboards.
- Merchants cannot access other merchants' store data.
- Disabled users cannot access protected APIs.

#### Ownership and Membership Model

In scope:

- Store owner relationship
- Store contributor relationship
- Merchant ownership checks
- Contributor store assignment checks
- Admin override permission checks

Acceptance criteria:

- Each store has an owner.
- Contributors are assigned to specific stores.
- Merchant-facing queries are scoped by store ownership.
- Contributor-facing queries are scoped by store assignment.
- Admin override behavior is explicit and auditable.

### 2. Multi-Tenant Store Management

The platform must support multiple independent stores, unique slugs, store-specific products, dashboard views, and store-scoped data access.

#### Store Creation

In scope:

- Create store
- Validate unique store slug
- Assign store owner
- Create default store profile
- Return store dashboard route

Acceptance criteria:

- A merchant can create one or more stores.
- Each store has a unique slug.
- Store creation assigns the current merchant as store owner.
- Public users can access a store through a route such as `/stores/{storeSlug}`.
- Store ownership is enforced at the backend service layer and database query layer.

#### Store Profile Management

In scope:

- Edit store name
- Edit store slug
- Edit store description
- Upload or update store avatar
- Update store availability
- Validate ownership before update

Acceptance criteria:

- Merchants can update only their own store profiles.
- Contributors can update store profile fields only when permission allows it.
- Store slug updates preserve uniqueness.
- Inactive or suspended stores are not publicly accessible.

#### Store Dashboard

In scope:

- View own store dashboard
- View store product list
- View store status
- View store contributor list
- Prevent access to another merchant's dashboard

Acceptance criteria:

- The merchant dashboard only shows data for the merchant's own store.
- Contributors can view only assigned store dashboards.
- Merchant A cannot view Merchant B's dashboard, products, or contributors.

#### Contributor Management

In scope:

- Add contributor
- View contributors
- Assign contributor permission
- Update contributor permission
- Remove contributor
- Approve contributor action
- Decline contributor action

Acceptance criteria:

- A merchant can add another user as a store contributor.
- Contributors receive equal or fewer permissions than merchants.
- Contributor permissions are checked before lifecycle operations.
- Merchant approval is supported for contributor actions that require review.

### 3. Product Management

Product management must support draft creation, editing, images, price, category, stock, private file attachment, publishing, unpublishing, and suspended status.

#### Product Draft Management

In scope:

- Create product draft
- Save product title
- Save product description
- Select category or subcategory
- Set product price
- Set stock quantity
- Save product status as `Draft`

Acceptance criteria:

- Merchants can create products as drafts.
- Contributors can create drafts only for assigned stores with permission.
- Draft products are never shown on public storefronts.
- Product price and stock are validated before publishing.

#### Product Media Management

In scope:

- Upload product image
- View product images
- Delete product image
- Attach private digital file
- Replace private digital file
- Store file metadata
- Prevent public access to private files

Acceptance criteria:

- Product images are publicly viewable only for published products.
- Private product files are never directly public.
- Private file metadata is stored with enough information to generate secure access after purchase.
- Replacing a private file preserves access control rules.

#### Product Editing

In scope:

- Edit own product
- Validate store ownership
- Validate contributor permission
- Prevent editing another store's product
- Preview drafted product

Acceptance criteria:

- Merchants can edit products in their own stores.
- Contributors can edit products only when assigned permission allows it.
- Merchant A cannot edit, publish, unpublish, or delete Merchant B's products.
- Product preview does not bypass public visibility rules.

#### Product Lifecycle

Supported statuses:

- `Draft`
- `Published`
- `Unpublished`
- `Suspended`

In scope:

- Validate product publishing eligibility
- Publish complete product
- Unpublish own product
- Republish unpublished product
- Suspend product by admin
- Restore suspended product if allowed
- Hide non-published products from storefront

Acceptance criteria:

- Only complete products can be published.
- Published products are visible on public storefronts and search.
- Unpublished products are hidden from storefronts and search.
- Suspended products are hidden and cannot be purchased.
- Admin can suspend or restore products according to policy.

### 4. Public Storefront and Product Discovery

Each store needs a public storefront showing store profile, product listing, categories, search, product details, stock, rating summary, and comments or reviews.

#### Public Storefront

In scope:

- View active store by slug
- Show store profile
- Show store product listing
- Hide inactive store
- Hide suspended store
- Hide unpublished products

Acceptance criteria:

- Public users can view active stores.
- Suspended or inactive stores are not publicly accessible.
- Public storefronts show only published products.

#### Product Listing

In scope:

- View published product list
- Show product image
- Show price
- Show category
- Show stock state
- Show rating summary
- Paginate product list

Acceptance criteria:

- Product listings include only products from active stores.
- Listings display enough product summary data for buyer discovery.
- Product list pagination prevents unbounded responses.

#### Product Detail

Product detail pages display:

- Title
- Description
- Images
- Price
- Category
- Stock quantity
- Average rating
- Review count
- Product reviews

Acceptance criteria:

- Public users can view published product details.
- Unpublished and suspended product detail pages are hidden from public users.
- Product review summaries exclude hidden or moderated reviews.

#### Search and Filtering

In scope:

- Search products by keyword
- Filter by category
- Filter by subcategory
- Filter by store
- Sort by price
- Sort by discount
- Sort by trend
- Return only published products from active stores
- Support typo-tolerant search

Acceptance criteria:

- Search results include only published products from active stores.
- Buyers can filter by category, subcategory, and store.
- Buyers can sort by price, discount, and trend.
- Typo-tolerant search is supported at an MVP-appropriate level.

### 5. Cart and Checkout

Cart and checkout must support cart modification, price calculation, authentication handling, pending order creation, payment session creation, and success, failure, or cancellation states.

#### Cart Management

In scope:

- Add published product to cart
- View cart
- Remove item from cart
- Update cart item state
- Persist cart across session
- Validate product availability
- Prevent unpublished product from being added
- Prevent suspended product from being added

Acceptance criteria:

- Authenticated buyers can add published products to cart.
- Buyers can view and remove cart items.
- Cart state remains consistent across sessions.
- Unpublished and suspended products cannot be added to cart.
- Product availability is revalidated before checkout.

#### Checkout Preparation

In scope:

- Require authentication before checkout
- Review cart before checkout
- Calculate subtotal
- Calculate total price
- Validate product stock
- Validate product price snapshot
- Create pending order
- Create order items from cart

Acceptance criteria:

- Buyer must sign in before checkout.
- Buyer can review cart before payment.
- System creates a pending order before payment confirmation.
- Order items preserve price snapshots for checkout consistency.

#### Checkout Completion

In scope:

- Create payment session
- Redirect to payment provider or mock checkout
- Handle successful checkout return
- Handle failed checkout return
- Handle cancelled checkout return
- Keep cart and order state consistent

Acceptance criteria:

- System creates a payment session or mock payment session.
- Buyer is redirected to the payment provider or mock checkout.
- Successful payments update order status.
- Failed or cancelled payments do not grant product access.

### 6. Payment and Platform Fees

The MVP payment layer includes payment session creation, platform fee calculation, payment confirmation, failure handling, status tracking, and idempotent processing.

#### Payment Session

In scope:

- Create payment session
- Store payment session identifier
- Associate payment with order
- Redirect buyer to payment flow
- Prevent duplicate active payment session when needed

Acceptance criteria:

- Payment sessions are tied to pending orders.
- Payment session identifiers are stored.
- Duplicate active sessions are handled consistently.

#### Payment Fee Calculation

In scope:

- Calculate platform fee from subtotal
- Store platform fee
- Store merchant receivable amount if needed
- Validate fee calculation consistency

Acceptance criteria:

- Platform fee is calculated from order subtotal.
- Fee calculation is persisted with payment or order data.
- Fee calculation is reproducible and testable.

#### Payment Status Management

In scope:

- Store payment status
- Update payment as succeeded
- Update payment as failed
- Update payment as cancelled
- Prevent duplicate charging effect
- Ensure payment processing is idempotent

Acceptance criteria:

- Payment status is stored.
- Payment success, failure, and cancellation are represented explicitly.
- Duplicate payment events do not create inconsistent order, payment, or access state.
- Payment processing is idempotent.

#### Order Update After Payment

In scope:

- Mark order as paid after payment success
- Mark order as failed after payment failure
- Mark order as cancelled after payment cancellation
- Do not grant access before confirmed payment
- Grant access after confirmed payment

Acceptance criteria:

- Product access is granted only after payment confirmation.
- Failed or cancelled payments do not grant access.
- Successful payment creates or confirms the buyer's access grant.

### 7. Buyer Library and Purchase History

The buyer library must show purchased products, purchase date, merchant name, availability state, and allow file access only after payment confirmation.

#### Purchase History

In scope:

- View own purchase history
- View order summary
- View purchased product list
- Show purchase date
- Show merchant name
- Prevent access to another buyer's purchase history

Acceptance criteria:

- Buyers can view only their own purchase history.
- Purchase history includes paid order summaries and purchased product data.
- Buyers cannot view another buyer's orders or purchases.

#### Buyer Product Library

In scope:

- View own product library
- Show products from paid orders
- Show product availability state
- Hide unpaid products
- Handle unavailable product state

Acceptance criteria:

- Buyers can view products from paid orders.
- Unpaid products are excluded from the library.
- Product availability is displayed.

#### Purchased File Access

In scope:

- Request purchased file access
- Verify buyer owns paid order
- Verify product file availability
- Generate secure file access URL
- Deny access to unpaid product file
- Deny access to another buyer's purchase

Acceptance criteria:

- Purchased files are accessible only after payment confirmation.
- File access checks buyer ownership, order payment status, and file availability.
- Secure file access URLs are generated only for eligible buyers.

### 8. Reviews and Ratings

Reviews must support star ratings, written reviews, one review per buyer per purchased product, aggregation, average rating, review count, and admin moderation.

#### Review Submission

In scope:

- Submit product review
- Verify completed purchase
- Enforce one review per buyer per product
- Validate rating between 1 and 5
- Save written review
- Reject review for unpurchased product

Acceptance criteria:

- Reviews require a completed purchase.
- One review is allowed per buyer per product.
- Ratings must be between 1 and 5.
- Reviews for unpurchased products are rejected.

#### Review Display

In scope:

- Display reviews on product page
- Display average rating
- Display review count
- Hide moderated reviews
- Refresh rating summary after new review

Acceptance criteria:

- Product pages display average rating and review count.
- Hidden or moderated reviews are excluded from public display.
- Rating summaries update after review changes.

#### Review Moderation

In scope:

- Admin views reviews
- Admin hides review
- Admin restores hidden review if allowed
- Product page excludes hidden reviews

Acceptance criteria:

- Admin can moderate or hide reviews.
- Hidden reviews do not appear on public product pages.
- Restored reviews can reappear when policy allows it.

### 9. Admin Dashboard

The admin dashboard includes user management, merchant management, store management, product visibility, category management, marketplace configuration, and review moderation.

#### Admin User Management

In scope:

- View users
- Search users
- Activate user
- Disable user
- View user detail

Acceptance criteria:

- Admin can view and search users.
- Admin can activate or disable users.
- Disabled users cannot sign in or access protected APIs.

#### Merchant and Store Management

In scope:

- View merchants
- View stores
- View store detail
- Suspend store
- Reactivate store if allowed
- View store products

Acceptance criteria:

- Admin can view merchants and stores.
- Admin can suspend stores.
- Suspended stores are hidden from public storefront access.

#### Product Visibility Management

In scope:

- View products
- View product detail
- Suspend product
- Hide product
- Restore product if allowed

Acceptance criteria:

- Admin can suspend or hide products.
- Hidden or suspended products are excluded from storefronts, search, and checkout.

#### Category Management

In scope:

- Create category
- Update category
- Disable category
- Assign parent category
- Validate category uniqueness

Acceptance criteria:

- Admin can create and update categories.
- Category names or slugs are unique according to the data model.
- Disabled categories cannot be selected for new published products.

#### Marketplace Configuration

In scope:

- View marketplace settings
- Update platform fee rate
- Update storefront visibility settings
- Update product policy settings if needed

Acceptance criteria:

- Admin can view marketplace settings.
- Admin can update platform fee configuration.
- Platform fee changes apply according to explicit business rules.

#### Review Moderation

In scope:

- View review list
- Hide review
- Restore review
- View product review summary

Acceptance criteria:

- Admin can review and moderate product reviews.
- Product review summaries reflect moderation state.

### 10. Observability, Testing, and Hardening

The MVP must include enough testing, error handling, logging, and security hardening to demonstrate correctness of tenant isolation, payments, file access, and role-based access.

#### Testing

In scope:

- Unit tests for services
- API integration tests
- Authorization tests
- Ownership tests
- Payment state tests
- File access tests
- Review eligibility tests

Acceptance criteria:

- Core business services have focused unit tests.
- API integration tests cover critical buyer, merchant, contributor, and admin workflows.
- Authorization and ownership tests prove cross-store access is rejected.
- Payment state tests prove success, failure, cancellation, and idempotency behavior.
- File access tests prove private files are not publicly accessible.

#### Error Handling

In scope:

- Standard validation error response
- Standard authentication error response
- Standard authorization error response
- Standard not-found response
- Standard payment failure response
- Standard upload failure response

Acceptance criteria:

- APIs use a consistent response and error convention.
- Validation errors include field-level details when applicable.
- Authentication and authorization failures are distinguishable without leaking sensitive data.
- Payment and upload failures are represented consistently.

#### Logging and Monitoring

In scope:

- Log authentication events
- Log protected API access failures
- Log payment status transitions
- Log file access requests
- Log admin moderation actions
- Add basic metrics

Acceptance criteria:

- Security-sensitive events are logged.
- Payment state transitions are traceable.
- File access requests are auditable.
- Admin moderation actions are auditable.
- Basic metrics exist for application health and critical workflows.

#### Security Hardening

In scope:

- Password hashing
- Secure session or token handling
- Input validation
- File upload validation
- Private file access control
- Rate limiting for auth endpoints
- CORS configuration

Acceptance criteria:

- Passwords are never stored in plaintext.
- Tokens or sessions are handled securely.
- User input is validated at API boundaries.
- Uploaded files are validated before storage.
- Private file access is always mediated by authorization checks.
- Authentication endpoints have basic rate limiting.
- CORS rules are explicit.

## Technical Scope

### In Scope

- Spring Boot backend
- Next.js frontend
- Oracle database
- Redis cache
- RabbitMQ for async events
- MinIO or S3-compatible storage
- Stripe test mode or mock payment adapter
- Docker Compose local development
- GitHub Actions CI
- Kubernetes or Minikube deployment baseline after MVP core flow works
- AWS deployment diagram and deployability assumptions

### Out of Scope

- Kafka
- Service mesh
- Distributed tracing stack
- Full production Kubernetes platform
- Terraform
- Multi-region deployment
- OpenSearch
- Complex observability stack

## Excluded Product Features

### Store and Tenant Exclusions

- Custom domain mapping
- Advanced tenant billing
- Tenant-specific themes
- Database-per-tenant architecture
- Full subdomain DNS automation

### Storefront and Discovery Exclusions

- Storefront theme customization
- Drag-and-drop storefront builder
- SEO optimization beyond basic page metadata
- Advanced product recommendations
- Personalized storefront layouts
- Real-time storefront analytics
- Semantic search
- Personalized ranking
- Recommendation engine
- Search analytics dashboard

### Product and Commerce Exclusions

- Product variants
- Product bundles
- Inventory warehouse logic
- Product version control
- Product approval workflow before publishing
- Subscription products
- Coupon codes
- Bulk product import
- Guest checkout
- Multiple payment methods
- Partial payments
- Buy Now Pay Later
- Installment payments
- Shared carts

### Payment and Finance Exclusions

- Merchant payouts
- Stripe Connect onboarding
- Tax calculation
- Refund automation
- Chargeback handling
- Payout reconciliation
- Invoice generation
- Multi-currency support

### Buyer Library and Reviews Exclusions

- Download analytics
- Expiring licenses
- Refund status display
- Subscription access
- Advanced receipts
- Invoice downloads
- Review replies
- Review voting
- User reporting
- Sentiment analysis
- Image or video reviews
- Verified badge customization
- Review recommendation ranking

### Authentication, Admin, and Operations Exclusions

- Social login
- Multi-factor authentication
- Passwordless login
- Enterprise SSO
- Account recovery workflow
- Device management
- Session management dashboard
- Custom roles
- Temporary access delegation
- Organization-level permission hierarchy
- Advanced analytics
- Revenue forecasting
- Fraud detection dashboard
- Customer support ticketing
- Audit log viewer
- Role permission editor
- Automated moderation rules
