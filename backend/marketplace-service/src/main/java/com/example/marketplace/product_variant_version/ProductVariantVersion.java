package com.example.marketplace.product_variant_version;

import java.util.Set;
import java.util.UUID;

import com.example.marketplace.money.Money;
import com.example.marketplace.shared.state_management.LifecycleStateMachine;

public final class ProductVariantVersion {
    private UUID id;
    private UUID productVersionId;
    private UUID productVariantId;
    private String sku;
    private String displayName;
    private Money price;
    private int sortOrder;
    private final LifecycleStateMachine<EProductVariantVersionStatus> status;

    public static ProductVariantVersion create(
        UUID id,
        String displayName,
        Money price,
        UUID productVariantId,
        UUID productVersionId,
        String sku,
        int sortOrder
    ) {
        return new ProductVariantVersion(id, displayName, price, productVersionId, productVariantId, sku, sortOrder);
    }

    private ProductVariantVersion(
        UUID id,
        String displayName,
        Money price,
        UUID productVersionId,
        UUID productVariantId,
        String sku,
        int sortOrder
    ) {
        this.setDisplayName(displayName);
        this.setId(id);
        this.status = new LifecycleStateMachine<EProductVariantVersionStatus>(
            "product_variant_version", id, EProductVariantVersionStatus.DRAFT
        );
        this.setPrice(price);
        this.setProductVariantId(productVariantId);
        this.setProductVersionId(productVersionId);
        this.setSku(sku);
        this.setSortOrder(sortOrder);
    }

    public EProductVariantVersionStatus submitForReview() {
        return this.status.transitionState(
            EProductVariantVersionStatus.IN_REVIEW,
            Set.of(EProductVariantVersionStatus.DRAFT)
        );
    }

    public EProductVariantVersionStatus reviseReview() {
        return this.status.transitionState(
            EProductVariantVersionStatus.DRAFT,
            Set.of(EProductVariantVersionStatus.IN_REVIEW)
        );
    }

    public EProductVariantVersionStatus approveReview() {
        return this.status.transitionState(
            EProductVariantVersionStatus.PUBLISHED,
            Set.of(EProductVariantVersionStatus.IN_REVIEW)
        );
    }

    public EProductVariantVersionStatus rejectReview() {
        return this.status.transitionState(
            EProductVariantVersionStatus.REJECTED,
            Set.of(EProductVariantVersionStatus.IN_REVIEW)
        );
    }

    public EProductVariantVersionStatus supersedeVersion() {
        return this.status.transitionState(
            EProductVariantVersionStatus.SUPERSEDED,
            Set.of(EProductVariantVersionStatus.PUBLISHED)
        );
    }

    public EProductVariantVersionStatus discard() {
        return this.status.transitionState(
            EProductVariantVersionStatus.ARCHIVED,
            Set.of(EProductVariantVersionStatus.DRAFT)
        );
    }

    public EProductVariantVersionStatus archive() {
        return this.status.transitionState(
            EProductVariantVersionStatus.ARCHIVED,
            Set.of(EProductVariantVersionStatus.REJECTED, EProductVariantVersionStatus.SUPERSEDED)
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProductVersionId() {
        return productVersionId;
    }

    public void setProductVersionId(UUID productVersionId) { this.productVersionId = productVersionId;  }

    public UUID getProductVariantId() {
        return productVariantId;
    }

    public void setProductVariantId(UUID productVariantId) {
        this.productVariantId = productVariantId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Money getPrice() {
        return price;
    }

    public void setPrice(Money price) {
        this.price = price;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public EProductVariantVersionStatus getPublicationStatus() {
        return status.getState();
    }
}
