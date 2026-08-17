package com.example.marketplace.product;

import java.util.Set;
import java.util.UUID;

import com.example.marketplace.shared.exception.InvalidStateTransitionException;

public final class Product {
    private UUID id;
    private UUID storeId;
    private String productName;
    private String productCode;
    private EProductStatus status;
    private UUID currentPublishedVersionId;

    public static Product create(UUID productId, UUID storeId, String productCode) {
        Product newProduct = new Product(productId, storeId, productCode);
        newProduct.setStatus(EProductStatus.ACTIVE);
        return newProduct;
    }

    private Product(UUID id, UUID storeId, String productCode) {
        this.setId(id);
        // this.setProductName(productName);
        this.setStoreId(storeId);
        this.setProductCode(productCode);
    }

    public EProductStatus discontinue() {
        return transitionState(
            EProductStatus.DISCONTINUED,
            Set.of(EProductStatus.ACTIVE)
        );
    }

    public EProductStatus resumeSelling() {
        return transitionState(
            EProductStatus.ACTIVE,
            Set.of(EProductStatus.DISCONTINUED)
        );
    }

    public EProductStatus archive() {
        return transitionState(
            EProductStatus.ARCHIVED,
            Set.of(EProductStatus.ACTIVE, EProductStatus.DISCONTINUED)
        );
    }

    private EProductStatus transitionState(EProductStatus targetState, Set<EProductStatus> allowedCurrentState) {
        EProductStatus currentState = this.getStatus();
        if (currentState == targetState) {
            return currentState;
        }
        else if (allowedCurrentState.contains(targetState)) {
            this.setStatus(targetState);
            return targetState;
        }
        else {
            throw new InvalidStateTransitionException(
                "product",
                this.getId(),
                this.getStatus(),
                targetState.toString()
            );
        }
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public void setStoreId(UUID storeId) {
        this.storeId = storeId;
    }

    public UUID getStoreId() {
        return storeId;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductName() {
        return productName;
    }

    public EProductStatus getStatus() {
        return status;
    }

    
    private void setStatus(EProductStatus status) {
        this.status = status;
    }

    public UUID getCurrentPublishedVersionId() {
        return this.currentPublishedVersionId;
    }

    public void setPublishedVersionId(UUID versionId) {
        this.currentPublishedVersionId = versionId;
    }
}
