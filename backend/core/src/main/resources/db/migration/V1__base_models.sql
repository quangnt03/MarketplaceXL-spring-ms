-- Type mappings:
--   UUID    -> RAW(16)
--   BOOLEAN -> NUMBER(1,0)
--   INT     -> NUMBER(10,0)
--   DECIMAL -> NUMBER(19,4)
--   TEXT    -> CLOB
--   JSON    -> CLOB with IS JSON validation

CREATE TABLE users (
    id RAW(16) NOT NULL,
    email VARCHAR2(320 CHAR) NOT NULL,
    display_name VARCHAR2(255 CHAR) NOT NULL,
    password_hash VARCHAR2(255 CHAR) NOT NULL,
    status VARCHAR2(50 CHAR) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    deleted_at TIMESTAMP(6),

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE auth_sessions (
    id RAW(16) NOT NULL,
    user_id RAW(16) NOT NULL,
    refresh_token_hash VARCHAR2(255 CHAR) NOT NULL,
    device_id VARCHAR2(255 CHAR),
    ip_address VARCHAR2(45 CHAR),
    user_agent VARCHAR2(1000 CHAR),
    status VARCHAR2(50 CHAR) NOT NULL,
    active_tenant_id RAW(16),
    active_store_id RAW(16),
    created_at TIMESTAMP(6) NOT NULL,
    last_used_at TIMESTAMP(6),
    expires_at TIMESTAMP(6) NOT NULL,
    revoked_at TIMESTAMP(6),

    CONSTRAINT pk_auth_sessions PRIMARY KEY (id)
);

CREATE TABLE roles (
    id RAW(16) NOT NULL,
    code VARCHAR2(100 CHAR) NOT NULL,
    scope_type VARCHAR2(50 CHAR) NOT NULL,
    description VARCHAR2(1000 CHAR),
    status VARCHAR2(50 CHAR) NOT NULL,
    is_system_role NUMBER(1,0) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,

    CONSTRAINT pk_roles PRIMARY KEY (id),
    CONSTRAINT uq_roles_code UNIQUE (code),
    CONSTRAINT ck_roles_system_bool
        CHECK (is_system_role IN (0, 1))
);

CREATE TABLE permissions (
    id RAW(16) NOT NULL,
    code VARCHAR2(150 CHAR) NOT NULL,
    "RESOURCE" VARCHAR2(100 CHAR) NOT NULL,
    action VARCHAR2(100 CHAR) NOT NULL,
    description VARCHAR2(1000 CHAR),
    created_at TIMESTAMP(6) NOT NULL,

    CONSTRAINT pk_permissions PRIMARY KEY (id),
    CONSTRAINT uq_permissions_code UNIQUE (code)
);

CREATE TABLE role_permissions (
    id RAW(16) NOT NULL,
    role_id RAW(16) NOT NULL,
    permission_id RAW(16) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,

    CONSTRAINT pk_role_permissions PRIMARY KEY (id)
);

CREATE TABLE platform_memberships (
    id RAW(16) NOT NULL,
    user_id RAW(16) NOT NULL,
    role_id RAW(16) NOT NULL,
    status VARCHAR2(50 CHAR) NOT NULL,
    assigned_by_user_id RAW(16),
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    removed_at TIMESTAMP(6),

    CONSTRAINT pk_platform_memberships PRIMARY KEY (id)
);

CREATE TABLE tenants (
    id RAW(16) NOT NULL,
    tenant_name VARCHAR2(255 CHAR) NOT NULL,
    tenant_email VARCHAR2(320 CHAR) NOT NULL,
    tenant_address VARCHAR2(1000 CHAR) NOT NULL,
    description VARCHAR2(2000 CHAR),
    status VARCHAR2(50 CHAR) NOT NULL,
    created_by_user_id RAW(16) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    deleted_at TIMESTAMP(6),

    CONSTRAINT pk_tenants PRIMARY KEY (id)
);

CREATE TABLE tenant_memberships (
    id RAW(16) NOT NULL,
    tenant_id RAW(16) NOT NULL,
    user_id RAW(16) NOT NULL,
    role_id RAW(16) NOT NULL,
    status VARCHAR2(50 CHAR) NOT NULL,
    invited_by_user_id RAW(16),
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    joined_at TIMESTAMP(6),
    removed_at TIMESTAMP(6),

    CONSTRAINT pk_tenant_memberships PRIMARY KEY (id)
);

CREATE TABLE tenant_invitations (
    id RAW(16) NOT NULL,
    tenant_id RAW(16) NOT NULL,
    email VARCHAR2(320 CHAR) NOT NULL,
    role_id RAW(16) NOT NULL,
    invited_by_user_id RAW(16) NOT NULL,
    accepted_by_user_id RAW(16),
    token_hash VARCHAR2(255 CHAR) NOT NULL,
    status VARCHAR2(50 CHAR) NOT NULL,
    expires_at TIMESTAMP(6) NOT NULL,
    accepted_at TIMESTAMP(6),
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,

    CONSTRAINT pk_tenant_invitations PRIMARY KEY (id)
);

CREATE TABLE tenant_verifications (
    id RAW(16) NOT NULL,
    tenant_id RAW(16) NOT NULL,
    submitted_by_user_id RAW(16) NOT NULL,
    reviewed_by_user_id RAW(16),
    business_name VARCHAR2(255 CHAR) NOT NULL,
    tax_code VARCHAR2(100 CHAR),
    document_url VARCHAR2(2048 CHAR),
    status VARCHAR2(50 CHAR) NOT NULL,
    rejection_reason VARCHAR2(2000 CHAR),
    submitted_at TIMESTAMP(6) NOT NULL,
    reviewed_at TIMESTAMP(6),
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,

    CONSTRAINT pk_tenant_verifications PRIMARY KEY (id)
);

CREATE TABLE stores (
    id RAW(16) NOT NULL,
    tenant_id RAW(16) NOT NULL,
    slug VARCHAR2(255 CHAR) NOT NULL,
    name VARCHAR2(255 CHAR) NOT NULL,
    description VARCHAR2(2000 CHAR),
    status VARCHAR2(50 CHAR) NOT NULL,
    created_by_user_id RAW(16) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    deleted_at TIMESTAMP(6),

    CONSTRAINT pk_stores PRIMARY KEY (id)
);

CREATE TABLE store_memberships (
    id RAW(16) NOT NULL,
    tenant_id RAW(16) NOT NULL,
    store_id RAW(16) NOT NULL,
    user_id RAW(16) NOT NULL,
    role_id RAW(16) NOT NULL,
    status VARCHAR2(50 CHAR) NOT NULL,
    assigned_by_user_id RAW(16),
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    removed_at TIMESTAMP(6),

    CONSTRAINT pk_store_memberships PRIMARY KEY (id)
);

CREATE TABLE product_categories (
    id RAW(16) NOT NULL,
    tenant_id RAW(16) NOT NULL,
    parent_category_id RAW(16),
    name VARCHAR2(255 CHAR) NOT NULL,
    slug VARCHAR2(255 CHAR) NOT NULL,
    description VARCHAR2(2000 CHAR),
    status VARCHAR2(50 CHAR) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,

    CONSTRAINT pk_product_categories PRIMARY KEY (id)
);

CREATE TABLE products (
    id RAW(16) NOT NULL,
    tenant_id RAW(16) NOT NULL,
    store_id RAW(16) NOT NULL,
    product_code VARCHAR2(100 CHAR) NOT NULL,
    lifecycle_status VARCHAR2(50 CHAR) NOT NULL,
    current_draft_version_id RAW(16),
    current_published_version_id RAW(16),
    created_by_user_id RAW(16) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    deleted_at TIMESTAMP(6),

    CONSTRAINT pk_products PRIMARY KEY (id),
    CONSTRAINT uq_products_product_code UNIQUE (product_code)
);

CREATE TABLE product_versions (
    id RAW(16) NOT NULL,
    product_id RAW(16) NOT NULL,
    category_id RAW(16),
    version_number NUMBER(10,0) NOT NULL,
    publication_status VARCHAR2(50 CHAR) NOT NULL,
    name VARCHAR2(255 CHAR) NOT NULL,
    slug VARCHAR2(255 CHAR) NOT NULL,
    description CLOB,
    short_description VARCHAR2(500 CHAR),
    metadata CLOB,
    change_note CLOB,
    based_on_version_id RAW(16),
    created_by_user_id RAW(16) NOT NULL,
    published_by_user_id RAW(16),
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    published_at TIMESTAMP(6),
    archived_at TIMESTAMP(6),

    CONSTRAINT pk_product_versions PRIMARY KEY (id),
    CONSTRAINT ck_product_versions_metadata_json
        CHECK (metadata IS JSON)
);

CREATE TABLE product_version_images (
    id RAW(16) NOT NULL,
    product_version_id RAW(16) NOT NULL,
    object_url VARCHAR2(2048 CHAR) NOT NULL,
    alt_text VARCHAR2(500 CHAR),
    sort_order NUMBER(10,0) NOT NULL,
    is_primary NUMBER(1,0) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,

    CONSTRAINT pk_product_version_images PRIMARY KEY (id),
    CONSTRAINT ck_product_version_images_primary_bool
        CHECK (is_primary IN (0, 1))
);

CREATE TABLE product_variants (
    id RAW(16) NOT NULL,
    product_id RAW(16) NOT NULL,
    variant_code VARCHAR2(100 CHAR) NOT NULL,
    status VARCHAR2(50 CHAR) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    deleted_at TIMESTAMP(6),

    CONSTRAINT pk_product_variants PRIMARY KEY (id),
    CONSTRAINT uq_product_variants_code UNIQUE (variant_code)
);

CREATE TABLE product_variant_versions (
    id RAW(16) NOT NULL,
    product_version_id RAW(16) NOT NULL,
    product_variant_id RAW(16) NOT NULL,
    sku VARCHAR2(100 CHAR) NOT NULL,
    name VARCHAR2(255 CHAR) NOT NULL,
    attributes CLOB NOT NULL,
    sort_order NUMBER(10,0) NOT NULL,
    publication_status VARCHAR2(50 CHAR) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,

    CONSTRAINT pk_product_variant_versions PRIMARY KEY (id),
    CONSTRAINT ck_product_variant_versions_attributes_json
        CHECK (attributes IS JSON)
);

CREATE TABLE product_version_prices (
    id RAW(16) NOT NULL,
    product_version_id RAW(16) NOT NULL,
    product_variant_id RAW(16),
    variant_version_id RAW(16),
    currency VARCHAR2(3 CHAR) NOT NULL,
    list_price NUMBER(19,4) NOT NULL,
    sale_price NUMBER(19,4),
    effective_from TIMESTAMP(6) NOT NULL,
    effective_to TIMESTAMP(6),
    status VARCHAR2(50 CHAR) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,

    CONSTRAINT pk_product_version_prices PRIMARY KEY (id)
);

CREATE TABLE inventory_items (
    id RAW(16) NOT NULL,
    tenant_id RAW(16) NOT NULL,
    store_id RAW(16) NOT NULL,
    product_id RAW(16) NOT NULL,
    variant_id RAW(16),
    quantity_on_hand NUMBER(10,0) NOT NULL,
    quantity_reserved NUMBER(10,0) NOT NULL,
    reorder_level NUMBER(10,0) NOT NULL,
    status VARCHAR2(50 CHAR) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,

    CONSTRAINT pk_inventory_items PRIMARY KEY (id)
);

CREATE TABLE inventory_movements (
    id RAW(16) NOT NULL,
    inventory_item_id RAW(16) NOT NULL,
    tenant_id RAW(16) NOT NULL,
    store_id RAW(16) NOT NULL,
    product_id RAW(16) NOT NULL,
    variant_id RAW(16),
    movement_type VARCHAR2(50 CHAR) NOT NULL,
    quantity NUMBER(10,0) NOT NULL,
    reference_type VARCHAR2(100 CHAR),
    reference_id RAW(16),
    reason VARCHAR2(1000 CHAR),
    created_by_user_id RAW(16) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,

    CONSTRAINT pk_inventory_movements PRIMARY KEY (id)
);

CREATE TABLE inventory_reservations (
    id RAW(16) NOT NULL,
    inventory_item_id RAW(16) NOT NULL,
    order_id RAW(16),
    order_item_id RAW(16),
    quantity NUMBER(10,0) NOT NULL,
    status VARCHAR2(50 CHAR) NOT NULL,
    expires_at TIMESTAMP(6),
    created_at TIMESTAMP(6) NOT NULL,
    released_at TIMESTAMP(6),

    CONSTRAINT pk_inventory_reservations PRIMARY KEY (id)
);

CREATE TABLE customer_profiles (
    id RAW(16) NOT NULL,
    user_id RAW(16) NOT NULL,
    display_name VARCHAR2(255 CHAR) NOT NULL,
    phone VARCHAR2(32 CHAR),
    default_shipping_address_id RAW(16),
    default_billing_address_id RAW(16),
    status VARCHAR2(50 CHAR) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,

    CONSTRAINT pk_customer_profiles PRIMARY KEY (id)
);

CREATE TABLE addresses (
    id RAW(16) NOT NULL,
    user_id RAW(16),
    tenant_id RAW(16),
    store_id RAW(16),
    address_type VARCHAR2(50 CHAR) NOT NULL,
    recipient_name VARCHAR2(255 CHAR),
    phone VARCHAR2(32 CHAR),
    address_line_1 VARCHAR2(500 CHAR) NOT NULL,
    address_line_2 VARCHAR2(500 CHAR),
    ward VARCHAR2(255 CHAR),
    district VARCHAR2(255 CHAR),
    city VARCHAR2(255 CHAR) NOT NULL,
    province VARCHAR2(255 CHAR),
    country VARCHAR2(255 CHAR) NOT NULL,
    postal_code VARCHAR2(32 CHAR),
    latitude NUMBER(10,7),
    longitude NUMBER(10,7),
    is_default NUMBER(1,0) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,

    CONSTRAINT pk_addresses PRIMARY KEY (id),
    CONSTRAINT ck_addresses_default_bool
        CHECK (is_default IN (0, 1))
);

CREATE TABLE carts (
    id RAW(16) NOT NULL,
    buyer_user_id RAW(16) NOT NULL,
    store_id RAW(16) NOT NULL,
    status VARCHAR2(50 CHAR) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    expires_at TIMESTAMP(6),

    CONSTRAINT pk_carts PRIMARY KEY (id)
);

CREATE TABLE cart_items (
    id RAW(16) NOT NULL,
    cart_id RAW(16) NOT NULL,
    product_id RAW(16) NOT NULL,
    variant_id RAW(16),
    product_version_id RAW(16) NOT NULL,
    product_variant_version_id RAW(16),
    quantity NUMBER(10,0) NOT NULL,
    unit_price NUMBER(19,4) NOT NULL,
    currency VARCHAR2(3 CHAR) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,

    CONSTRAINT pk_cart_items PRIMARY KEY (id)
);

CREATE TABLE orders (
    id RAW(16) NOT NULL,
    order_number VARCHAR2(100 CHAR) NOT NULL,
    tenant_id RAW(16) NOT NULL,
    store_id RAW(16) NOT NULL,
    buyer_user_id RAW(16) NOT NULL,
    shipping_address_id RAW(16),
    billing_address_id RAW(16),
    status VARCHAR2(50 CHAR) NOT NULL,
    payment_status VARCHAR2(50 CHAR) NOT NULL,
    fulfillment_status VARCHAR2(50 CHAR) NOT NULL,
    subtotal_amount NUMBER(19,4) NOT NULL,
    discount_amount NUMBER(19,4) NOT NULL,
    shipping_amount NUMBER(19,4) NOT NULL,
    tax_amount NUMBER(19,4) NOT NULL,
    total_amount NUMBER(19,4) NOT NULL,
    currency VARCHAR2(3 CHAR) NOT NULL,
    note CLOB,
    placed_at TIMESTAMP(6) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    cancelled_at TIMESTAMP(6),

    CONSTRAINT pk_orders PRIMARY KEY (id),
    CONSTRAINT uq_orders_order_number UNIQUE (order_number)
);

CREATE TABLE order_items (
    id RAW(16) NOT NULL,
    order_id RAW(16) NOT NULL,
    product_id RAW(16) NOT NULL,
    variant_id RAW(16),
    product_version_id RAW(16) NOT NULL,
    product_variant_version_id RAW(16),
    product_name VARCHAR2(255 CHAR) NOT NULL,
    sku VARCHAR2(100 CHAR) NOT NULL,
    quantity NUMBER(10,0) NOT NULL,
    unit_price NUMBER(19,4) NOT NULL,
    discount_amount NUMBER(19,4) NOT NULL,
    total_amount NUMBER(19,4) NOT NULL,
    currency VARCHAR2(3 CHAR) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,

    CONSTRAINT pk_order_items PRIMARY KEY (id)
);

CREATE TABLE order_status_history (
    id RAW(16) NOT NULL,
    order_id RAW(16) NOT NULL,
    from_status VARCHAR2(50 CHAR),
    to_status VARCHAR2(50 CHAR) NOT NULL,
    changed_by_user_id RAW(16),
    reason VARCHAR2(1000 CHAR),
    created_at TIMESTAMP(6) NOT NULL,

    CONSTRAINT pk_order_status_history PRIMARY KEY (id)
);

CREATE TABLE payments (
    id RAW(16) NOT NULL,
    order_id RAW(16) NOT NULL,
    tenant_id RAW(16) NOT NULL,
    store_id RAW(16) NOT NULL,
    buyer_user_id RAW(16) NOT NULL,
    provider VARCHAR2(100 CHAR) NOT NULL,
    provider_payment_id VARCHAR2(255 CHAR),
    amount NUMBER(19,4) NOT NULL,
    currency VARCHAR2(3 CHAR) NOT NULL,
    status VARCHAR2(50 CHAR) NOT NULL,
    failure_reason VARCHAR2(2000 CHAR),
    paid_at TIMESTAMP(6),
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,

    CONSTRAINT pk_payments PRIMARY KEY (id)
);

CREATE TABLE payment_transactions (
    id RAW(16) NOT NULL,
    payment_id RAW(16) NOT NULL,
    transaction_type VARCHAR2(50 CHAR) NOT NULL,
    provider_transaction_id VARCHAR2(255 CHAR),
    amount NUMBER(19,4) NOT NULL,
    currency VARCHAR2(3 CHAR) NOT NULL,
    status VARCHAR2(50 CHAR) NOT NULL,
    raw_response CLOB,
    created_at TIMESTAMP(6) NOT NULL,

    CONSTRAINT pk_payment_transactions PRIMARY KEY (id),
    CONSTRAINT ck_payment_transactions_raw_json
        CHECK (raw_response IS JSON)
);

CREATE TABLE refunds (
    id RAW(16) NOT NULL,
    payment_id RAW(16) NOT NULL,
    order_id RAW(16) NOT NULL,
    requested_by_user_id RAW(16) NOT NULL,
    amount NUMBER(19,4) NOT NULL,
    currency VARCHAR2(3 CHAR) NOT NULL,
    reason VARCHAR2(2000 CHAR),
    status VARCHAR2(50 CHAR) NOT NULL,
    provider_refund_id VARCHAR2(255 CHAR),
    requested_at TIMESTAMP(6) NOT NULL,
    processed_at TIMESTAMP(6),

    CONSTRAINT pk_refunds PRIMARY KEY (id)
);

CREATE TABLE shipments (
    id RAW(16) NOT NULL,
    order_id RAW(16) NOT NULL,
    tenant_id RAW(16) NOT NULL,
    store_id RAW(16) NOT NULL,
    carrier VARCHAR2(100 CHAR),
    tracking_number VARCHAR2(255 CHAR),
    shipping_address_id RAW(16) NOT NULL,
    status VARCHAR2(50 CHAR) NOT NULL,
    shipped_at TIMESTAMP(6),
    delivered_at TIMESTAMP(6),
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,

    CONSTRAINT pk_shipments PRIMARY KEY (id)
);

CREATE TABLE shipment_items (
    id RAW(16) NOT NULL,
    shipment_id RAW(16) NOT NULL,
    order_item_id RAW(16) NOT NULL,
    quantity NUMBER(10,0) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,

    CONSTRAINT pk_shipment_items PRIMARY KEY (id)
);

CREATE TABLE notifications (
    id RAW(16) NOT NULL,
    user_id RAW(16),
    tenant_id RAW(16),
    store_id RAW(16),
    channel VARCHAR2(50 CHAR) NOT NULL,
    notification_type VARCHAR2(100 CHAR) NOT NULL,
    subject VARCHAR2(500 CHAR),
    body CLOB NOT NULL,
    status VARCHAR2(50 CHAR) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    sent_at TIMESTAMP(6),
    failed_at TIMESTAMP(6),

    CONSTRAINT pk_notifications PRIMARY KEY (id)
);

CREATE TABLE notification_templates (
    id RAW(16) NOT NULL,
    code VARCHAR2(100 CHAR) NOT NULL,
    channel VARCHAR2(50 CHAR) NOT NULL,
    subject_template VARCHAR2(500 CHAR),
    body_template CLOB NOT NULL,
    status VARCHAR2(50 CHAR) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,

    CONSTRAINT pk_notification_templates PRIMARY KEY (id),
    CONSTRAINT uq_notification_templates_code UNIQUE (code)
);

CREATE TABLE audit_logs (
    id RAW(16) NOT NULL,
    actor_user_id RAW(16) NOT NULL,
    tenant_id RAW(16),
    store_id RAW(16),
    action VARCHAR2(100 CHAR) NOT NULL,
    resource_type VARCHAR2(100 CHAR) NOT NULL,
    resource_id RAW(16) NOT NULL,
    before_data CLOB,
    after_data CLOB,
    ip_address VARCHAR2(45 CHAR),
    user_agent VARCHAR2(1000 CHAR),
    created_at TIMESTAMP(6) NOT NULL,

    CONSTRAINT pk_audit_logs PRIMARY KEY (id),
    CONSTRAINT ck_audit_logs_before_json
        CHECK (before_data IS JSON),
    CONSTRAINT ck_audit_logs_after_json
        CHECK (after_data IS JSON)
);
