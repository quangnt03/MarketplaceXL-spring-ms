package com.example.marketplace.product_variant;

import java.util.Set;
import java.util.UUID;

import com.example.marketplace.inventory.Inventory;
import com.example.marketplace.shared.exception.DuplicateInventoryException;
import com.example.marketplace.shared.state_management.LifecycleStateMachine;

public class ProductVariant {
    private UUID id;
    private UUID productId;
    private UUID tenantId;
    private UUID storeId;
    private String variantCode;
    private Inventory inventory;
    private LifecycleStateMachine<EProductVariantStatus> publicationStatus;

    public static ProductVariant create(UUID id, UUID productId, String variantCode) {
        return new ProductVariant(id, productId, variantCode);
    }

    private ProductVariant(UUID id, UUID productId, String variantCode) {
        this.setProductId(productId);
        this.setId(id);
        this.publicationStatus = new LifecycleStateMachine<EProductVariantStatus>(
            "product_variant", id, EProductVariantStatus.ACTIVE
        );
        this.setVariantCode(variantCode);
    }

    public Inventory initializeInventory(int quantity, int reorderLevel) {
        UUID inventoryId = UUID.randomUUID();
        int defaultReorderLevel = 1;
        if (this.inventory != null) {
            throw new DuplicateInventoryException(this.getId());
        }
        this.inventory = Inventory.create(
            inventoryId,
            this.getProductId(),
            this.getId(),
            quantity,
            quantity,
            defaultReorderLevel
        );

        return inventory;
    }

    public EProductVariantStatus discontinue() {
        return this.publicationStatus.transitionState(
            EProductVariantStatus.DISCONTINUED,
            Set.of(EProductVariantStatus.ACTIVE)
        );
    }

    public EProductVariantStatus resumeSelling() {
        return this.publicationStatus.transitionState(
            EProductVariantStatus.ACTIVE,
            Set.of(EProductVariantStatus.DISCONTINUED)
        );
    }

    public EProductVariantStatus archive() {
        return this.publicationStatus.transitionState(
            EProductVariantStatus.ARCHIVED,
            Set.of(EProductVariantStatus.ACTIVE, EProductVariantStatus.DISCONTINUED)
        );
    }

    public Inventory getInventory() {
        return inventory;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public UUID getStoreId() {
        return storeId;
    }

    public void setStoreId(UUID storeId) {
        this.storeId = storeId;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public String getVariantCode() {
        return variantCode;
    }

    public void setVariantCode(String variantCode) {
        this.variantCode = variantCode;
    }

    public EProductVariantStatus getStatus() {
        return publicationStatus.getState();
    }
}
