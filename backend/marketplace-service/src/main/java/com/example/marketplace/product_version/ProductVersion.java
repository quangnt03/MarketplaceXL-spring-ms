package com.example.marketplace.product_version;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import com.example.marketplace.product_variant.EProductVariantStatus;
import com.example.marketplace.shared.exception.*;
import com.example.marketplace.product_variant.ProductVariant;
import com.example.marketplace.product_variant_version.ProductVariantVersion;
import com.example.marketplace.shared.state_management.LifecycleStateMachine;

public final class ProductVersion {
    private UUID id;
    private UUID productId;
    private UUID categoryId;
    private int versionNumber;
    private LifecycleStateMachine<EProductVersionStatus> publicationStatus;
    private String productName;
    private String description;
    private List<String> mediaReferences;

    public static ProductVersion create(UUID id, UUID productId, int versionNumber) {
        return new ProductVersion(id, productId, versionNumber);
    }

    private ProductVersion(UUID id, UUID productId, int versionNumber) {
        this.setId(id);
        this.publicationStatus = new LifecycleStateMachine<EProductVersionStatus>(
            "product_version", id, EProductVersionStatus.DRAFT
        );
        this.setProductId(productId);
        this.setVersionNumber(versionNumber);
    }

    public void updateDetails(String name, String description, UUID categoryId, List<String> mediaReferences) {
        this.setProductName(name);
        this.setDescription(description);
        this.setCategoryId(categoryId);
        this.setMediaReferences(mediaReferences);
    }

    public EProductVersionStatus passToReview() {
        return this.publicationStatus.transitionState(
            EProductVersionStatus.IN_REVIEW,
            Set.of(EProductVersionStatus.DRAFT)
        );
    }

    public EProductVersionStatus withdrawReviewSubmission() {
        return this.publicationStatus.transitionState(
            EProductVersionStatus.DRAFT,
            Set.of(EProductVersionStatus.IN_REVIEW)
        );
    }

    public EProductVersionStatus rejectReview() {
        return this.publicationStatus.transitionState(
            EProductVersionStatus.REJECTED,
            Set.of(EProductVersionStatus.IN_REVIEW)
        );
    }

    public EProductVersionStatus reviseReviewSubmission() {
        return this.publicationStatus.transitionState(
            EProductVersionStatus.DRAFT,
            Set.of(EProductVersionStatus.REJECTED)
        );
    }

    public EProductVersionStatus approvePublish() {
        return this.publicationStatus.transitionState(
            EProductVersionStatus.PUBLISHED,
            Set.of(EProductVersionStatus.IN_REVIEW)
        );
    }

    public EProductVersionStatus supersedeVersion() {
        return this.publicationStatus.transitionState(
            EProductVersionStatus.SUPERSEDED,
            Set.of(EProductVersionStatus.PUBLISHED)
        );
    }

    public EProductVersionStatus archive() {
        return this.publicationStatus.transitionState(
            EProductVersionStatus.ARCHIVED,
            Set.of(
                EProductVersionStatus.DRAFT,
                EProductVersionStatus.REJECTED,
                EProductVersionStatus.SUPERSEDED
            )
        );
    }

    public void submitForReview(List<ProductVariant> productVariants, List<ProductVariantVersion> offers) {
        // check if there are any variants and versions passed in
        if (productVariants.isEmpty() || offers.isEmpty()) {
            throw new ProductReadinessException(ProductReadinessFailure.NO_ACTIVE_VARIANT);
        }

        if (this.getDescription() == null || this.getDescription().isBlank()) {
            throw new ProductReadinessException(ProductReadinessFailure.MISSING_DESCRIPTION);
        }

        if (this.getCategoryId() == null) {
            throw new ProductReadinessException(ProductReadinessFailure.MISSING_CATEGORY);
        }

        if (this.getName() == null || this.getName().isBlank()) {
            throw new ProductReadinessException(ProductReadinessFailure.MISSING_NAME);
        }

        AtomicBoolean hasActiveVariant = new AtomicBoolean(false);

        productVariants.forEach(productVariant -> {
            if(productVariant.getStatus() == EProductVariantStatus.ACTIVE) {
                hasActiveVariant.set(true);
            }
        });
        if (!hasActiveVariant.get()) {
            throw new ProductReadinessException(ProductReadinessFailure.NO_ACTIVE_VARIANT);
        }
        HashSet<String> existingSku = new HashSet<>();
        offers.forEach(variantVersion -> {
            if (variantVersion.getSku().isBlank()) {
                throw new ProductReadinessException(ProductReadinessFailure.INVALID_SKU);
            }
            if (variantVersion.getDisplayName().isBlank()) {
                throw new ProductReadinessException(ProductReadinessFailure.MISSING_NAME);
            }
            if (existingSku.contains(variantVersion.getSku().toLowerCase(Locale.ROOT))) {
                throw new ProductReadinessException(ProductReadinessFailure.DUPLICATE_SKU);
            }
            existingSku.add(variantVersion.getSku().toLowerCase(Locale.ROOT));
            if (variantVersion.getPrice() == null
                || variantVersion.getPrice().getAmount().compareTo(BigDecimal.ZERO) <= 0
            ) {
                throw new ProductReadinessException(ProductReadinessFailure.INVALID_PRICE);
            }
        });


        HashMap<UUID, ProductVariant> productVariantExists = new HashMap<UUID, ProductVariant>();
        productVariants.forEach(productVariant -> {
            // validate matched variant product id with version product id
            if (!productVariant.getProductId().equals(this.getProductId())) {
                throw new OwnershipMismatchException(
                    "product_variant",
                    productVariant.getId(),
                    "product",
                    this.getProductId(),
                    productVariant.getProductId()
                );
            }
            // validate the variant is in valid state
            if (!productVariant.getStatus().equals(EProductVariantStatus.ACTIVE)) {
                throw new InvalidStatusOperationException(
                    "product_variant",
                    productVariant.getId(),
                    productVariant.getStatus(),
                    "product_version.submit_for_approval"
                );
            }
            productVariantExists.put(productVariant.getId(), productVariant);
        });

        offers.forEach(variantVersion -> {
            if (!variantVersion.getProductVersionId().equals(this.getId())) {
                throw new OwnershipMismatchException(
                    "product_variant_version",
                    variantVersion.getId(),
                    "product_version",
                    this.getId(),
                    variantVersion.getProductVersionId()
                );
            }

            if (!productVariantExists.containsKey(variantVersion.getProductVariantId())) {
                throw new OwnershipMismatchException(
                    "product_variant_version",
                    variantVersion.getId(),
                    "product_variant",
                    new Object(),
                    variantVersion.getProductVariantId()
                );
            }
            variantVersion.submitForReview();
        });
        this.passToReview();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public EProductVersionStatus getPublicationStatus() {
        return publicationStatus.getState();
    }

    public String getName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public List<String> getMediaReferences() {
        return mediaReferences;
    }

    public void setMediaReferences(List<String> mediaReferences) {
        this.mediaReferences = mediaReferences;
    }

}
