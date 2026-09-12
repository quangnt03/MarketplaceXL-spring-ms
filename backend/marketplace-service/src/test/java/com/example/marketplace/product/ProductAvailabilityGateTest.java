package com.example.marketplace.product;

import static com.example.marketplace.product.ProductLifecycleFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.marketplace.shared.exception.InvalidStatusOperationException;
import com.example.marketplace.shared.exception.OwnershipMismatchException;
import com.example.marketplace.product_variant_version.EProductVariantVersionStatus;
import com.example.marketplace.product_variant_version.ProductVariantVersion;
import com.example.marketplace.product_version.EProductVersionStatus;
import com.example.marketplace.product_version.ProductVersion;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * Product's own availability status gates submission and publication of its children.
 * See specification.md decision log for this aggregate-boundary rule.
 */
class ProductAvailabilityGateTest {

    @Nested
    @DisplayName("submitVersionForReview requires an ACTIVE product")
    class SubmissionGate {

        @Test
        void activeProductDelegatesToVersionSubmission() {
            Product product = Product.create(PRODUCT_ID, STORE_ID, "P1");
            ProductVersion version = completeDraftVersion();
            List<ProductVariantVersion> offers = validOffers();

            product.submitVersionForReview(version, activeVariants(), offers);

            assertThat(version.getPublicationStatus()).isEqualTo(EProductVersionStatus.IN_REVIEW);
            assertThat(offers)
                    .extracting(ProductVariantVersion::getPublicationStatus)
                    .containsOnly(EProductVariantVersionStatus.IN_REVIEW);
        }

        @ParameterizedTest
        @EnumSource(value = EProductStatus.class, names = "ACTIVE", mode = EnumSource.Mode.EXCLUDE)
        void nonActiveProductRejectsSubmissionWithoutMutation(EProductStatus status) {
            Product product = Product.create(PRODUCT_ID, STORE_ID, "P1");
            applyStatus(product, status);
            ProductVersion version = completeDraftVersion();
            List<ProductVariantVersion> offers = validOffers();

            assertThatThrownBy(() -> product.submitVersionForReview(version, activeVariants(), offers))
                    .isInstanceOfSatisfying(
                            InvalidStatusOperationException.class,
                            ex -> assertThat(ex.getCurrentStatus()).isEqualTo(status.name()));

            assertThat(version.getPublicationStatus()).isEqualTo(EProductVersionStatus.DRAFT);
            assertThat(offers)
                    .extracting(ProductVariantVersion::getPublicationStatus)
                    .containsOnly(EProductVariantVersionStatus.DRAFT);
        }

        @Test
        void versionBelongingToAnotherProductIsRejected() {
            Product product = Product.create(PRODUCT_ID, STORE_ID, "P1");
            ProductVersion version = ProductVersion.create(VERSION_ID, OTHER_PRODUCT_ID, 1);

            assertThatThrownBy(() -> product.submitVersionForReview(version, List.of(), List.of()))
                    .isInstanceOf(OwnershipMismatchException.class);
        }
    }

    @Nested
    @DisplayName("publish requires an ACTIVE product")
    class PublicationGate {

        @ParameterizedTest
        @EnumSource(value = EProductStatus.class, names = "ACTIVE", mode = EnumSource.Mode.EXCLUDE)
        void productBecomingNonActiveAfterSubmissionRejectsPublishWithoutMutation(EProductStatus status) {
            Product product = Product.create(PRODUCT_ID, STORE_ID, "P1");
            ProductVersion version = completeDraftVersion();
            List<ProductVariantVersion> offers = validOffers();
            product.submitVersionForReview(version, activeVariants(), offers);

            applyStatus(product, status);

            assertThatThrownBy(() -> product.publish(version, offers))
                    .isInstanceOfSatisfying(
                            InvalidStatusOperationException.class,
                            ex -> assertThat(ex.getCurrentStatus()).isEqualTo(status.name()));

            assertThat(version.getPublicationStatus()).isEqualTo(EProductVersionStatus.IN_REVIEW);
            assertThat(offers)
                    .extracting(ProductVariantVersion::getPublicationStatus)
                    .containsOnly(EProductVariantVersionStatus.IN_REVIEW);
            assertThat(product.getCurrentPublishedVersionId()).isNull();
        }
    }

    private static void applyStatus(Product product, EProductStatus status) {
        switch (status) {
            case DISCONTINUED -> product.discontinue();
            case ARCHIVED -> product.archive();
            case ACTIVE -> { }
        }
    }
}