package com.example.marketplace.product_version;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.example.marketplace.product.Product;
import static com.example.marketplace.product.ProductLifecycleFixtures.CATEGORY_ID;
import static com.example.marketplace.product.ProductLifecycleFixtures.MEDIUM_VARIANT_ID;
import static com.example.marketplace.product.ProductLifecycleFixtures.PRODUCT_ID;
import static com.example.marketplace.product.ProductLifecycleFixtures.SMALL_VARIANT_ID;
import static com.example.marketplace.product.ProductLifecycleFixtures.STORE_ID;
import static com.example.marketplace.product.ProductLifecycleFixtures.activeVariants;
import static com.example.marketplace.product.ProductLifecycleFixtures.completeDraftVersion;
import static com.example.marketplace.product.ProductLifecycleFixtures.discontinuedVariant;
import static com.example.marketplace.product.ProductLifecycleFixtures.draftVersion;
import static com.example.marketplace.product.ProductLifecycleFixtures.offerWithPrice;
import static com.example.marketplace.product.ProductLifecycleFixtures.validOffer;
import static com.example.marketplace.product.ProductLifecycleFixtures.validOffers;
import com.example.marketplace.product_variant.ProductVariant;
import com.example.marketplace.product_variant_version.EProductVariantVersionStatus;
import com.example.marketplace.product_variant_version.ProductVariantVersion;
import com.example.marketplace.shared.exception.IllegalLifecycleTransitionException;
import com.example.marketplace.shared.exception.ProductReadinessException;
import com.example.marketplace.shared.exception.ProductReadinessFailure;

/**
 * ProductVersion submission for review: readiness validation and the DRAFT to IN_REVIEW transition.
 * Covers AC-07, AC-08, and the submission-side cases of AC-10.
 * See artifacts/product-publication-lifecycle/specification.md.
 */
class ProductVersionSubmissionTest {

    @Test
    void ac07_submitCompleteVersionMovesParentAndOffersToInReview() {
        ProductVersion version = completeDraftVersion();
        List<ProductVariantVersion> offers = validOffers();

        version.submitForReview(activeVariants(), offers);

        assertThat(version.getPublicationStatus()).isEqualTo(EProductVersionStatus.IN_REVIEW);
        assertThat(offers)
                .extracting(ProductVariantVersion::getPublicationStatus)
                .containsOnly(EProductVariantVersionStatus.IN_REVIEW);
    }

    @ParameterizedTest(name = "AC-08 rejects incomplete version: {0}")
    @MethodSource("incompleteVersions")
    void ac08_submitRejectsIncompleteVersionWithoutPartialStateChange(
            String description,
            ProductVersion version,
            List<ProductVariant> variants,
            List<ProductVariantVersion> offers,
            ProductReadinessFailure expectedFailure) {

        assertThatThrownBy(() -> version.submitForReview(variants, offers))
                .isInstanceOfSatisfying(
                        ProductReadinessException.class,
                        exception -> assertThat(exception.getFailure()).isEqualTo(expectedFailure));

        assertThat(version.getPublicationStatus()).isEqualTo(EProductVersionStatus.DRAFT);
        if (offers.isEmpty()) {
            assertThat(offers).isEmpty();
        } else {
            assertThat(offers)
                .extracting(ProductVariantVersion::getPublicationStatus)
                .containsOnly(EProductVariantVersionStatus.DRAFT);
        }
    }

    @Test
    void ac10_submitOutsideDraftIsRejectedWithoutMutation() {
        ProductVersion version = completeDraftVersion();
        List<ProductVariantVersion> offers = validOffers();
        version.submitForReview(activeVariants(), offers);

        assertThatThrownBy(() -> version.submitForReview(activeVariants(), offers))
                .isInstanceOf(IllegalLifecycleTransitionException.class)
                .hasMessageContaining("IN_REVIEW");

        assertThat(version.getPublicationStatus()).isEqualTo(EProductVersionStatus.IN_REVIEW);
        assertThat(offers)
                .extracting(ProductVariantVersion::getPublicationStatus)
                .containsOnly(EProductVariantVersionStatus.IN_REVIEW);
    }

    @Test
    void ac10_submitPublishedVersionIsRejectedWithoutMutation() {
        Product product = Product.create(PRODUCT_ID, STORE_ID, "P1");
        ProductVersion version = completeDraftVersion();
        List<ProductVariantVersion> offers = validOffers();
        version.submitForReview(activeVariants(), offers);
        product.publish(version, offers);

        assertThatThrownBy(() -> version.submitForReview(activeVariants(), offers))
                .isInstanceOf(IllegalLifecycleTransitionException.class)
                .hasMessageContaining("PUBLISHED");

        assertThat(version.getPublicationStatus()).isEqualTo(EProductVersionStatus.PUBLISHED);
        assertThat(offers)
                .extracting(ProductVariantVersion::getPublicationStatus)
                .containsOnly(EProductVariantVersionStatus.PUBLISHED);
    }

    @Test
    void ac10_submitRejectsChildOutsideDraftWithoutMutation() {
        ProductVersion reviewedVersion = completeDraftVersion();
        List<ProductVariantVersion> offers = validOffers();
        reviewedVersion.submitForReview(activeVariants(), offers);
        ProductVersion draftVersion = completeDraftVersion();

        assertThatThrownBy(() -> draftVersion.submitForReview(activeVariants(), offers))
                .isInstanceOf(IllegalLifecycleTransitionException.class)
                .hasMessageContaining("IN_REVIEW");

        assertThat(draftVersion.getPublicationStatus()).isEqualTo(EProductVersionStatus.DRAFT);
        assertThat(offers)
                .extracting(ProductVariantVersion::getPublicationStatus)
                .containsOnly(EProductVariantVersionStatus.IN_REVIEW);
    }

    private static Stream<Arguments> incompleteVersions() {
        return Stream.of(
                Arguments.of(
                        "missing name",
                        draftVersion(null, "Description", CATEGORY_ID),
                        activeVariants(),
                        validOffers(),
                        ProductReadinessFailure.MISSING_NAME),
                Arguments.of(
                        "missing description",
                        draftVersion("Name", " ", CATEGORY_ID),
                        activeVariants(),
                        validOffers(),
                        ProductReadinessFailure.MISSING_DESCRIPTION),
                Arguments.of(
                        "missing category",
                        draftVersion("Name", "Description", null),
                        activeVariants(),
                        validOffers(),
                        ProductReadinessFailure.MISSING_CATEGORY),
                Arguments.of(
                        "no active variant",
                        completeDraftVersion(),
                        List.of(discontinuedVariant()),
                        List.of(validOffer(SMALL_VARIANT_ID, "BLACK-S", "100.00", 1)),
                        ProductReadinessFailure.NO_ACTIVE_VARIANT),
                Arguments.of(
                        "no variants or offers",
                        completeDraftVersion(),
                        List.of(),
                        List.of(),
                        ProductReadinessFailure.NO_ACTIVE_VARIANT),
                Arguments.of(
                        "blank SKU",
                        completeDraftVersion(),
                        activeVariants(),
                        List.of(validOffer(SMALL_VARIANT_ID, " ", "100.00", 1)),
                        ProductReadinessFailure.INVALID_SKU),
                Arguments.of(
                        "duplicate SKU",
                        completeDraftVersion(),
                        activeVariants(),
                        List.of(
                                validOffer(SMALL_VARIANT_ID, "BLACK", "100.00", 1),
                                validOffer(MEDIUM_VARIANT_ID, "black", "110.00", 2)),
                        ProductReadinessFailure.DUPLICATE_SKU),
                Arguments.of(
                        "zero price",
                        completeDraftVersion(),
                        activeVariants(),
                        List.of(validOffer(SMALL_VARIANT_ID, "BLACK-S", "0.00", 1)),
                        ProductReadinessFailure.INVALID_PRICE),
                Arguments.of(
                        "negative price",
                        completeDraftVersion(),
                        activeVariants(),
                        List.of(validOffer(SMALL_VARIANT_ID, "BLACK-S", "-1.00", 1)),
                        ProductReadinessFailure.INVALID_PRICE),
                Arguments.of(
                        "null price",
                        completeDraftVersion(),
                        activeVariants(),
                        List.of(offerWithPrice(SMALL_VARIANT_ID, "BLACK-S", null, 1)),
                        ProductReadinessFailure.INVALID_PRICE));
    }
}
