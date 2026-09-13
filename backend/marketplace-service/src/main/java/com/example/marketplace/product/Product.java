package com.example.marketplace.product;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.example.marketplace.product_variant.ProductVariant;
import com.example.marketplace.product_variant_version.EProductVariantVersionStatus;
import com.example.marketplace.shared.exception.IllegalLifecycleTransitionException;
import com.example.marketplace.shared.exception.InvalidStatusOperationException;
import com.example.marketplace.shared.exception.OwnershipMismatchException;
import com.example.marketplace.product_variant_version.ProductVariantVersion;
import com.example.marketplace.product_version.ProductVersion;
import com.example.marketplace.shared.state_management.LifecycleStateMachine;

public final class Product {
    private UUID id;
    private UUID storeId;
    private String productName;
    private String productCode;
    private final LifecycleStateMachine<EProductStatus> status;
    private UUID currentPublishedVersionId;

    private static final String ENTITY_TYPE_PRODUCT = "product";
    private static final String ENTITY_TYPE_PRODUCT_VERSION = "product_version";

    public static Product create(UUID productId, UUID storeId, String productCode) {
        return new Product(
            productId,
            storeId,
            productCode
        );
    }

    private Product(UUID id, UUID storeId, String productCode) {
        this.setId(id);
        this.status = new LifecycleStateMachine<EProductStatus>(
            ENTITY_TYPE_PRODUCT, id, EProductStatus.ACTIVE
        );
        this.setPublishedVersionId(null);
        this.setStoreId(storeId);
        this.setProductCode(productCode);
    }

    public EProductStatus discontinue() {
        return this.status.transitionState(
            EProductStatus.DISCONTINUED,
            Set.of(EProductStatus.ACTIVE)
        );
    }

    public EProductStatus resumeSelling() {
        return this.status.transitionState(
            EProductStatus.ACTIVE,
            Set.of(EProductStatus.DISCONTINUED)
        );
    }

    public EProductStatus archive() {
        return this.status.transitionState(
            EProductStatus.ARCHIVED,
            Set.of(EProductStatus.ACTIVE, EProductStatus.DISCONTINUED)
        );
    }

    public void submitVersionForReview(
        ProductVersion version,
        List<ProductVariant> variants,
        List<ProductVariantVersion> offers
    ) {
        if (this.getStatus() != EProductStatus.ACTIVE) {
            throw new InvalidStatusOperationException(
                ENTITY_TYPE_PRODUCT,
                this.getId(),
                this.getStatus(),
                "product_version_submit_for_review"
            );
        }

        if (!version.getProductId().equals(this.getId())) {
            throw new OwnershipMismatchException(
                ENTITY_TYPE_PRODUCT_VERSION,
                version.getId(),
                ENTITY_TYPE_PRODUCT,
                this.getId(),
                version.getProductId()
            );
        }

        version.submitForReview(variants, offers);

    }

    public void publish(ProductVersion version, List<ProductVariantVersion> offers) {
        if (offers.isEmpty()) {
            // if offer is null, create a pseudo id instead
            Object pseudoChildIdObj = new Object();
            throw new OwnershipMismatchException(
                "product_variant_version",
                pseudoChildIdObj,
                ENTITY_TYPE_PRODUCT_VERSION,
                version.getId().toString(),
                version.getId().toString()
            );
        }

        if (this.getStatus() != EProductStatus.ACTIVE) {
            throw new InvalidStatusOperationException(
                ENTITY_TYPE_PRODUCT,
                this.getId(),
                this.getStatus(),
                "product_publish"
            );
        }

        if (!version.getProductId().equals(this.getId())) {
            throw new OwnershipMismatchException(
                ENTITY_TYPE_PRODUCT_VERSION,
                version.getId(),
                ENTITY_TYPE_PRODUCT,
                this.getId(),
                version.getProductId()
            );
        }

        offers.forEach(variantVersion -> {
            if (!variantVersion.getProductVersionId().equals(version.getId())) {
                throw new OwnershipMismatchException(
                    "product_variant_version",
                    variantVersion.getId(),
                    ENTITY_TYPE_PRODUCT_VERSION,
                    version.getId(),
                    variantVersion.getProductVersionId()
                );
            }
            if (variantVersion.getPublicationStatus() != EProductVariantVersionStatus.IN_REVIEW) {
                throw new IllegalLifecycleTransitionException(
                    "product_variant_version",
                    variantVersion.getId(),
                    variantVersion.getPublicationStatus(),
                    "PUBLISHED"
                );
            }
        });

        offers.forEach(ProductVariantVersion::approveReview);
        version.approvePublish();
        setPublishedVersionId(version.getId());
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
        return status.getState();
    }

    public UUID getCurrentPublishedVersionId() {
        return this.currentPublishedVersionId;
    }

    public void setPublishedVersionId(UUID versionId) {
        this.currentPublishedVersionId = versionId;
    }

}
