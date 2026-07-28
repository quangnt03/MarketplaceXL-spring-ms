
ALTER TABLE auth_sessions
    ADD CONSTRAINT fk_auth_sessions_user
    FOREIGN KEY (user_id)
    REFERENCES users (id);

ALTER TABLE auth_sessions
    ADD CONSTRAINT fk_auth_sessions_tenant
    FOREIGN KEY (active_tenant_id)
    REFERENCES tenants (id);

ALTER TABLE auth_sessions
    ADD CONSTRAINT fk_auth_sessions_store
    FOREIGN KEY (active_store_id)
    REFERENCES stores (id);

ALTER TABLE role_permissions
    ADD CONSTRAINT fk_role_permissions_role
    FOREIGN KEY (role_id)
    REFERENCES roles (id);

ALTER TABLE role_permissions
    ADD CONSTRAINT fk_role_permissions_permission
    FOREIGN KEY (permission_id)
    REFERENCES permissions (id);

ALTER TABLE platform_memberships
    ADD CONSTRAINT fk_platform_memberships_user
    FOREIGN KEY (user_id)
    REFERENCES users (id);

ALTER TABLE platform_memberships
    ADD CONSTRAINT fk_platform_memberships_role
    FOREIGN KEY (role_id)
    REFERENCES roles (id);

ALTER TABLE platform_memberships
    ADD CONSTRAINT fk_platform_memberships_assigner
    FOREIGN KEY (assigned_by_user_id)
    REFERENCES users (id);

ALTER TABLE tenants
    ADD CONSTRAINT fk_tenants_creator
    FOREIGN KEY (created_by_user_id)
    REFERENCES users (id);

ALTER TABLE tenant_memberships
    ADD CONSTRAINT fk_tenant_memberships_tenant
    FOREIGN KEY (tenant_id)
    REFERENCES tenants (id);

ALTER TABLE tenant_memberships
    ADD CONSTRAINT fk_tenant_memberships_user
    FOREIGN KEY (user_id)
    REFERENCES users (id);

ALTER TABLE tenant_memberships
    ADD CONSTRAINT fk_tenant_memberships_role
    FOREIGN KEY (role_id)
    REFERENCES roles (id);

ALTER TABLE tenant_memberships
    ADD CONSTRAINT fk_tenant_memberships_inviter
    FOREIGN KEY (invited_by_user_id)
    REFERENCES users (id);

ALTER TABLE tenant_invitations
    ADD CONSTRAINT fk_tenant_invitations_tenant
    FOREIGN KEY (tenant_id)
    REFERENCES tenants (id);

ALTER TABLE tenant_invitations
    ADD CONSTRAINT fk_tenant_invitations_role
    FOREIGN KEY (role_id)
    REFERENCES roles (id);

ALTER TABLE tenant_invitations
    ADD CONSTRAINT fk_tenant_invitations_inviter
    FOREIGN KEY (invited_by_user_id)
    REFERENCES users (id);

ALTER TABLE tenant_invitations
    ADD CONSTRAINT fk_tenant_invitations_accepter
    FOREIGN KEY (accepted_by_user_id)
    REFERENCES users (id);

ALTER TABLE tenant_verifications
    ADD CONSTRAINT fk_tenant_verifications_tenant
    FOREIGN KEY (tenant_id)
    REFERENCES tenants (id);

ALTER TABLE tenant_verifications
    ADD CONSTRAINT fk_tenant_verifications_submitter
    FOREIGN KEY (submitted_by_user_id)
    REFERENCES users (id);

ALTER TABLE tenant_verifications
    ADD CONSTRAINT fk_tenant_verifications_reviewer
    FOREIGN KEY (reviewed_by_user_id)
    REFERENCES users (id);

ALTER TABLE stores
    ADD CONSTRAINT fk_stores_tenant
    FOREIGN KEY (tenant_id)
    REFERENCES tenants (id);

ALTER TABLE stores
    ADD CONSTRAINT fk_stores_creator
    FOREIGN KEY (created_by_user_id)
    REFERENCES users (id);

ALTER TABLE store_memberships
    ADD CONSTRAINT fk_store_memberships_tenant
    FOREIGN KEY (tenant_id)
    REFERENCES tenants (id);

ALTER TABLE store_memberships
    ADD CONSTRAINT fk_store_memberships_store
    FOREIGN KEY (store_id)
    REFERENCES stores (id);

ALTER TABLE store_memberships
    ADD CONSTRAINT fk_store_memberships_user
    FOREIGN KEY (user_id)
    REFERENCES users (id);

ALTER TABLE store_memberships
    ADD CONSTRAINT fk_store_memberships_role
    FOREIGN KEY (role_id)
    REFERENCES roles (id);

ALTER TABLE store_memberships
    ADD CONSTRAINT fk_store_memberships_assigner
    FOREIGN KEY (assigned_by_user_id)
    REFERENCES users (id);

ALTER TABLE product_categories
    ADD CONSTRAINT fk_product_categories_tenant
    FOREIGN KEY (tenant_id)
    REFERENCES tenants (id);

ALTER TABLE product_categories
    ADD CONSTRAINT fk_product_categories_parent
    FOREIGN KEY (parent_category_id)
    REFERENCES product_categories (id);

ALTER TABLE products
    ADD CONSTRAINT fk_products_tenant
    FOREIGN KEY (tenant_id)
    REFERENCES tenants (id);

ALTER TABLE products
    ADD CONSTRAINT fk_products_store
    FOREIGN KEY (store_id)
    REFERENCES stores (id);

ALTER TABLE products
    ADD CONSTRAINT fk_products_creator
    FOREIGN KEY (created_by_user_id)
    REFERENCES users (id);

ALTER TABLE products
    ADD CONSTRAINT fk_products_draft_version
    FOREIGN KEY (current_draft_version_id)
    REFERENCES product_versions (id);

ALTER TABLE products
    ADD CONSTRAINT fk_products_published_version
    FOREIGN KEY (current_published_version_id)
    REFERENCES product_versions (id);

ALTER TABLE product_versions
    ADD CONSTRAINT fk_product_versions_product
    FOREIGN KEY (product_id)
    REFERENCES products (id);

ALTER TABLE product_versions
    ADD CONSTRAINT fk_product_versions_category
    FOREIGN KEY (category_id)
    REFERENCES product_categories (id);

ALTER TABLE product_versions
    ADD CONSTRAINT fk_product_versions_base
    FOREIGN KEY (based_on_version_id)
    REFERENCES product_versions (id);

ALTER TABLE product_versions
    ADD CONSTRAINT fk_product_versions_creator
    FOREIGN KEY (created_by_user_id)
    REFERENCES users (id);

ALTER TABLE product_versions
    ADD CONSTRAINT fk_product_versions_publisher
    FOREIGN KEY (published_by_user_id)
    REFERENCES users (id);

ALTER TABLE product_version_images
    ADD CONSTRAINT fk_product_version_images_version
    FOREIGN KEY (product_version_id)
    REFERENCES product_versions (id);

ALTER TABLE product_variants
    ADD CONSTRAINT fk_product_variants_product
    FOREIGN KEY (product_id)
    REFERENCES products (id);

ALTER TABLE product_variant_versions
    ADD CONSTRAINT fk_product_variant_versions_version
    FOREIGN KEY (product_version_id)
    REFERENCES product_versions (id);

ALTER TABLE product_variant_versions
    ADD CONSTRAINT fk_product_variant_versions_variant
    FOREIGN KEY (product_variant_id)
    REFERENCES product_variants (id);

ALTER TABLE product_version_prices
    ADD CONSTRAINT fk_product_version_prices_version
    FOREIGN KEY (product_version_id)
    REFERENCES product_versions (id);

ALTER TABLE product_version_prices
    ADD CONSTRAINT fk_product_version_prices_variant
    FOREIGN KEY (product_variant_id)
    REFERENCES product_variants (id);

ALTER TABLE product_version_prices
    ADD CONSTRAINT fk_product_version_prices_variant_ver
    FOREIGN KEY (variant_version_id)
    REFERENCES product_variant_versions (id);

ALTER TABLE inventory_items
    ADD CONSTRAINT fk_inventory_items_tenant
    FOREIGN KEY (tenant_id)
    REFERENCES tenants (id);

ALTER TABLE inventory_items
    ADD CONSTRAINT fk_inventory_items_store
    FOREIGN KEY (store_id)
    REFERENCES stores (id);

ALTER TABLE inventory_items
    ADD CONSTRAINT fk_inventory_items_product
    FOREIGN KEY (product_id)
    REFERENCES products (id);

ALTER TABLE inventory_items
    ADD CONSTRAINT fk_inventory_items_variant
    FOREIGN KEY (variant_id)
    REFERENCES product_variants (id);

ALTER TABLE inventory_movements
    ADD CONSTRAINT fk_inventory_movements_item
    FOREIGN KEY (inventory_item_id)
    REFERENCES inventory_items (id);

ALTER TABLE inventory_movements
    ADD CONSTRAINT fk_inventory_movements_tenant
    FOREIGN KEY (tenant_id)
    REFERENCES tenants (id);

ALTER TABLE inventory_movements
    ADD CONSTRAINT fk_inventory_movements_store
    FOREIGN KEY (store_id)
    REFERENCES stores (id);

ALTER TABLE inventory_movements
    ADD CONSTRAINT fk_inventory_movements_product
    FOREIGN KEY (product_id)
    REFERENCES products (id);

ALTER TABLE inventory_movements
    ADD CONSTRAINT fk_inventory_movements_variant
    FOREIGN KEY (variant_id)
    REFERENCES product_variants (id);

ALTER TABLE inventory_movements
    ADD CONSTRAINT fk_inventory_movements_creator
    FOREIGN KEY (created_by_user_id)
    REFERENCES users (id);

ALTER TABLE inventory_reservations
    ADD CONSTRAINT fk_inventory_reservations_item
    FOREIGN KEY (inventory_item_id)
    REFERENCES inventory_items (id);

ALTER TABLE inventory_reservations
    ADD CONSTRAINT fk_inventory_reservations_order
    FOREIGN KEY (order_id)
    REFERENCES orders (id);

ALTER TABLE inventory_reservations
    ADD CONSTRAINT fk_inventory_reservations_order_item
    FOREIGN KEY (order_item_id)
    REFERENCES order_items (id);

ALTER TABLE customer_profiles
    ADD CONSTRAINT fk_customer_profiles_user
    FOREIGN KEY (user_id)
    REFERENCES users (id);

ALTER TABLE customer_profiles
    ADD CONSTRAINT fk_customer_profiles_ship_addr
    FOREIGN KEY (default_shipping_address_id)
    REFERENCES addresses (id);

ALTER TABLE customer_profiles
    ADD CONSTRAINT fk_customer_profiles_bill_addr
    FOREIGN KEY (default_billing_address_id)
    REFERENCES addresses (id);

ALTER TABLE addresses
    ADD CONSTRAINT fk_addresses_user
    FOREIGN KEY (user_id)
    REFERENCES users (id);

ALTER TABLE addresses
    ADD CONSTRAINT fk_addresses_tenant
    FOREIGN KEY (tenant_id)
    REFERENCES tenants (id);

ALTER TABLE addresses
    ADD CONSTRAINT fk_addresses_store
    FOREIGN KEY (store_id)
    REFERENCES stores (id);

ALTER TABLE carts
    ADD CONSTRAINT fk_carts_buyer
    FOREIGN KEY (buyer_user_id)
    REFERENCES users (id);

ALTER TABLE carts
    ADD CONSTRAINT fk_carts_store
    FOREIGN KEY (store_id)
    REFERENCES stores (id);

ALTER TABLE cart_items
    ADD CONSTRAINT fk_cart_items_cart
    FOREIGN KEY (cart_id)
    REFERENCES carts (id);

ALTER TABLE cart_items
    ADD CONSTRAINT fk_cart_items_product
    FOREIGN KEY (product_id)
    REFERENCES products (id);

ALTER TABLE cart_items
    ADD CONSTRAINT fk_cart_items_variant
    FOREIGN KEY (variant_id)
    REFERENCES product_variants (id);

ALTER TABLE cart_items
    ADD CONSTRAINT fk_cart_items_product_version
    FOREIGN KEY (product_version_id)
    REFERENCES product_versions (id);

ALTER TABLE cart_items
    ADD CONSTRAINT fk_cart_items_variant_version
    FOREIGN KEY (product_variant_version_id)
    REFERENCES product_variant_versions (id);

ALTER TABLE orders
    ADD CONSTRAINT fk_orders_tenant
    FOREIGN KEY (tenant_id)
    REFERENCES tenants (id);

ALTER TABLE orders
    ADD CONSTRAINT fk_orders_store
    FOREIGN KEY (store_id)
    REFERENCES stores (id);

ALTER TABLE orders
    ADD CONSTRAINT fk_orders_buyer
    FOREIGN KEY (buyer_user_id)
    REFERENCES users (id);

ALTER TABLE orders
    ADD CONSTRAINT fk_orders_shipping_addr
    FOREIGN KEY (shipping_address_id)
    REFERENCES addresses (id);

ALTER TABLE orders
    ADD CONSTRAINT fk_orders_billing_addr
    FOREIGN KEY (billing_address_id)
    REFERENCES addresses (id);

ALTER TABLE order_items
    ADD CONSTRAINT fk_order_items_order
    FOREIGN KEY (order_id)
    REFERENCES orders (id);

ALTER TABLE order_items
    ADD CONSTRAINT fk_order_items_product
    FOREIGN KEY (product_id)
    REFERENCES products (id);

ALTER TABLE order_items
    ADD CONSTRAINT fk_order_items_variant
    FOREIGN KEY (variant_id)
    REFERENCES product_variants (id);

ALTER TABLE order_items
    ADD CONSTRAINT fk_order_items_product_version
    FOREIGN KEY (product_version_id)
    REFERENCES product_versions (id);

ALTER TABLE order_items
    ADD CONSTRAINT fk_order_items_variant_version
    FOREIGN KEY (product_variant_version_id)
    REFERENCES product_variant_versions (id);

ALTER TABLE order_status_history
    ADD CONSTRAINT fk_order_status_history_order
    FOREIGN KEY (order_id)
    REFERENCES orders (id);

ALTER TABLE order_status_history
    ADD CONSTRAINT fk_order_status_history_changer
    FOREIGN KEY (changed_by_user_id)
    REFERENCES users (id);

ALTER TABLE payments
    ADD CONSTRAINT fk_payments_order
    FOREIGN KEY (order_id)
    REFERENCES orders (id);

ALTER TABLE payments
    ADD CONSTRAINT fk_payments_tenant
    FOREIGN KEY (tenant_id)
    REFERENCES tenants (id);

ALTER TABLE payments
    ADD CONSTRAINT fk_payments_store
    FOREIGN KEY (store_id)
    REFERENCES stores (id);

ALTER TABLE payments
    ADD CONSTRAINT fk_payments_buyer
    FOREIGN KEY (buyer_user_id)
    REFERENCES users (id);

ALTER TABLE payment_transactions
    ADD CONSTRAINT fk_payment_transactions_payment
    FOREIGN KEY (payment_id)
    REFERENCES payments (id);

ALTER TABLE refunds
    ADD CONSTRAINT fk_refunds_payment
    FOREIGN KEY (payment_id)
    REFERENCES payments (id);

ALTER TABLE refunds
    ADD CONSTRAINT fk_refunds_order
    FOREIGN KEY (order_id)
    REFERENCES orders (id);

ALTER TABLE refunds
    ADD CONSTRAINT fk_refunds_requester
    FOREIGN KEY (requested_by_user_id)
    REFERENCES users (id);

ALTER TABLE shipments
    ADD CONSTRAINT fk_shipments_order
    FOREIGN KEY (order_id)
    REFERENCES orders (id);

ALTER TABLE shipments
    ADD CONSTRAINT fk_shipments_tenant
    FOREIGN KEY (tenant_id)
    REFERENCES tenants (id);

ALTER TABLE shipments
    ADD CONSTRAINT fk_shipments_store
    FOREIGN KEY (store_id)
    REFERENCES stores (id);

ALTER TABLE shipments
    ADD CONSTRAINT fk_shipments_address
    FOREIGN KEY (shipping_address_id)
    REFERENCES addresses (id);

ALTER TABLE shipment_items
    ADD CONSTRAINT fk_shipment_items_shipment
    FOREIGN KEY (shipment_id)
    REFERENCES shipments (id);

ALTER TABLE shipment_items
    ADD CONSTRAINT fk_shipment_items_order_item
    FOREIGN KEY (order_item_id)
    REFERENCES order_items (id);

ALTER TABLE notifications
    ADD CONSTRAINT fk_notifications_user
    FOREIGN KEY (user_id)
    REFERENCES users (id);

ALTER TABLE notifications
    ADD CONSTRAINT fk_notifications_tenant
    FOREIGN KEY (tenant_id)
    REFERENCES tenants (id);

ALTER TABLE notifications
    ADD CONSTRAINT fk_notifications_store
    FOREIGN KEY (store_id)
    REFERENCES stores (id);

ALTER TABLE audit_logs
    ADD CONSTRAINT fk_audit_logs_actor
    FOREIGN KEY (actor_user_id)
    REFERENCES users (id);

ALTER TABLE audit_logs
    ADD CONSTRAINT fk_audit_logs_tenant
    FOREIGN KEY (tenant_id)
    REFERENCES tenants (id);

ALTER TABLE audit_logs
    ADD CONSTRAINT fk_audit_logs_store
    FOREIGN KEY (store_id)
    REFERENCES stores (id);
