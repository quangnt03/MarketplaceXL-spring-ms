package com.example.marketplace.inventory;

import java.util.Set;
import java.util.UUID;

import com.example.marketplace.shared.exception.InvalidValueException;
import com.example.marketplace.shared.state_management.LifecycleStateMachine;

public final class Inventory {
    private UUID id;
    private UUID productId;
    private UUID variantId;

    private int quantityOnHand;
    private int quantityReserved;
    private int reorderLevel;

    private final LifecycleStateMachine<InventoryItemStatus> status;

    public static Inventory create(
        UUID id,
        UUID productId,
        UUID variantId,
        int quantityOnHand,
        int quantityReserved,
        int reorderLevel
    ) {
        return new Inventory(id, productId, variantId, quantityOnHand, quantityReserved, reorderLevel);
    }

    private Inventory(
        UUID id,
        UUID productId,
        UUID variantId,
        int quantityOnHand,
        int quantityReserved,
        int reorderLevel
    ) {
        this.setId(id);
        this.status = new LifecycleStateMachine<InventoryItemStatus>(
            "inventory", id, InventoryItemStatus.ACTIVE
        );
        this.setProductId(productId);
        this.setProductVariantId(variantId);
        this.setQuantityOnHand(quantityOnHand);
        this.setQuantityReserved(quantityReserved);
        this.setReorderLevel(reorderLevel);
    }

    public InventoryItemStatus disableReservation() {
        return this.status.transitionState(
            InventoryItemStatus.INACTIVE,
            Set.of(InventoryItemStatus.ACTIVE)
        );
    }

    public InventoryItemStatus enableReservation() {
        return this.status.transitionState(
            InventoryItemStatus.ACTIVE,
            Set.of(InventoryItemStatus.INACTIVE)
        );
    }

    public InventoryItemStatus archiveInventory() {
        return this.status.transitionState(
            InventoryItemStatus.ARCHIVED,
            Set.of(InventoryItemStatus.ACTIVE, InventoryItemStatus.INACTIVE)
        );
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

    public UUID getProductVariantId() {
        return variantId;
    }

    public void setProductVariantId(UUID variantId) {
        this.variantId = variantId;
    }

    public int getAvailableQuantity() {
        return quantityOnHand;
    }

    public void setQuantityOnHand(int quantityOnHand) {
        if (quantityOnHand < 0) {
            throw new InvalidValueException(
                "availableQuantity",
                "> 0",
                "inventory",
                this.getId().toString()
            );
        }
        this.quantityOnHand = quantityOnHand;
    }

    public int getQuantityReserved() {
        return quantityReserved;
    }

    public void setQuantityReserved(int quantityReserved) {
        if (quantityReserved < 0) {
            throw new InvalidValueException(
                "reservedQuantity",
                "> 0",
                "inventory",
                this.getId().toString()
            );
        }
        this.quantityReserved = quantityReserved;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public InventoryItemStatus getStatus() {
        return status.getState();
    }
}
