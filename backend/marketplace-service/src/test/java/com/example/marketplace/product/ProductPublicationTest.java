package com.example.marketplace.product;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.example.marketplace.product.ProductLifecycleFixtures.EXTRA_OFFER_ID;
import static com.example.marketplace.product.ProductLifecycleFixtures.EXTRA_VARIANT_ID;
import static com.example.marketplace.product.ProductLifecycleFixtures.PRODUCT_ID;
import static com.example.marketplace.product.ProductLifecycleFixtures.STORE_ID;
import static com.example.marketplace.product.ProductLifecycleFixtures.VERSION_ID;
import static com.example.marketplace.product.ProductLifecycleFixtures.activeVariants;
import static com.example.marketplace.product.ProductLifecycleFixtures.completeDraftVersion;
import static com.example.marketplace.product.ProductLifecycleFixtures.money;
import static com.example.marketplace.product.ProductLifecycleFixtures.validOffers;
import com.example.marketplace.product_variant_version.EProductVariantVersionStatus;
import com.example.marketplace.product_variant_version.ProductVariantVersion;
import com.example.marketplace.product_version.EProductVersionStatus;
import com.example.marketplace.product_version.ProductVersion;
import com.example.marketplace.shared.exception.IllegalLifecycleTransitionException;

/**
 * Product publication: coordinated IN_REVIEW to PUBLISHED transition and currentPublishedVersionId assignment.
 * Covers AC-09, the publish-side cases of AC-10, and AC-12.
 * See artifacts/product-publication-lifecycle/specification.md.
 */
class ProductPublicationTest {

    @Nested
    @DisplayName("AC-09: publishing an IN_REVIEW version")
    class SuccessfulPublication {

        @Test
        void ac09_publishInReviewVersionPublishesOffersAndUpdatesProductPointer() {
            Product product = Product.create(PRODUCT_ID, STORE_ID, "P1");
            ProductVersion version = completeDraftVersion();
            List<ProductVariantVersion> offers = validOffers();
            version.submitForReview(activeVariants(), offers);

            product.publish(version, offers);

            assertThat(version.getPublicationStatus()).isEqualTo(EProductVersionStatus.PUBLISHED);
            assertThat(offers)
                    .extracting(ProductVariantVersion::getPublicationStatus)
                    .containsOnly(EProductVariantVersionStatus.PUBLISHED);
            assertThat(product.getCurrentPublishedVersionId()).isEqualTo(VERSION_ID);
        }
    }

    @Nested
    @DisplayName("AC-10: illegal publish transitions leave the aggregate unchanged")
    class IllegalTransitions {

        @Test
        void ac10_publishOutsideInReviewIsRejectedWithoutMutation() {
            Product product = Product.create(PRODUCT_ID, STORE_ID, "P1");
            ProductVersion version = completeDraftVersion();
            List<ProductVariantVersion> offers = validOffers();

            assertThatThrownBy(() -> product.publish(version, offers))
                    .isInstanceOf(IllegalLifecycleTransitionException.class)
                    .hasMessageContaining("DRAFT")
                    .hasMessageContaining("PUBLISHED");

            assertThat(version.getPublicationStatus()).isEqualTo(EProductVersionStatus.DRAFT);
            assertThat(offers)
                    .extracting(ProductVariantVersion::getPublicationStatus)
                    .containsOnly(EProductVariantVersionStatus.DRAFT);
            assertThat(product.getCurrentPublishedVersionId()).isNull();
        }

        @Test
        void ac10_repeatedPublishIsRejectedWithoutMutation() {
            Product product = Product.create(PRODUCT_ID, STORE_ID, "P1");
            ProductVersion version = completeDraftVersion();
            List<ProductVariantVersion> offers = validOffers();
            version.submitForReview(activeVariants(), offers);
            product.publish(version, offers);

            assertThatThrownBy(() -> product.publish(version, offers))
                    .isInstanceOfSatisfying(
                            IllegalLifecycleTransitionException.class,
                            ex -> {
                                assertThat(ex.getSourceState()).isEqualTo("PUBLISHED");
                                assertThat(ex.getAttemptedAction()).isEqualTo("PUBLISHED");
                            });

            assertThat(version.getPublicationStatus()).isEqualTo(EProductVersionStatus.PUBLISHED);
            assertThat(offers)
                    .extracting(ProductVariantVersion::getPublicationStatus)
                    .containsOnly(EProductVariantVersionStatus.PUBLISHED);
            assertThat(product.getCurrentPublishedVersionId()).isEqualTo(VERSION_ID);
        }
        
        @Test
        void ac10_repeatedApprovePublishOnVersionAloneIsRejectedWithoutMutation() {
            ProductVersion version = completeDraftVersion();
            List<ProductVariantVersion> offers = validOffers();
            version.submitForReview(activeVariants(), offers);
            version.approvePublish();

            assertThatThrownBy(version::approvePublish)
                    .isInstanceOfSatisfying(
                            IllegalLifecycleTransitionException.class,
                            ex -> {
                                assertThat(ex.getEntityType()).isEqualTo("product_version");
                                assertThat(ex.getSourceState()).isEqualTo("PUBLISHED");
                            });

            assertThat(version.getPublicationStatus()).isEqualTo(EProductVersionStatus.PUBLISHED);
        }
    }

    @Nested
    @DisplayName("AC-12: a single invalid child aborts the whole publish")
    class AtomicFailure {

        @Test
        void ac12_failedChildPublicationLeavesTheWholeAggregateUnchanged() {
            Product product = Product.create(PRODUCT_ID, STORE_ID, "P1");
            ProductVersion version = completeDraftVersion();
            List<ProductVariantVersion> offers = new ArrayList<>(validOffers());
            version.submitForReview(activeVariants(), offers);
            offers.add(ProductVariantVersion.create(
                    EXTRA_OFFER_ID,
                    "Black / Large",
                    money("120.00"),
                    EXTRA_VARIANT_ID,
                    VERSION_ID,
                    "BLACK-L",
                    3));

            assertThatThrownBy(() -> product.publish(version, offers))
                    .isInstanceOf(IllegalLifecycleTransitionException.class);

            assertThat(version.getPublicationStatus()).isEqualTo(EProductVersionStatus.IN_REVIEW);
            assertThat(offers.get(0).getPublicationStatus()).isEqualTo(EProductVariantVersionStatus.IN_REVIEW);
            assertThat(offers.get(1).getPublicationStatus()).isEqualTo(EProductVariantVersionStatus.IN_REVIEW);
            assertThat(offers.get(2).getPublicationStatus()).isEqualTo(EProductVariantVersionStatus.DRAFT);
            assertThat(product.getCurrentPublishedVersionId()).isNull();
        }
    }
}
