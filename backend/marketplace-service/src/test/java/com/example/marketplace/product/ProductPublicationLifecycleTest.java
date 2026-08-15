package com.example.marketplace.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.marketplace.inventory.Inventory;
import com.example.marketplace.money.Money;
import com.example.marketplace.product.exception.DuplicateInventoryException;
import com.example.marketplace.product.exception.IllegalLifecycleTransitionException;
import com.example.marketplace.product.exception.OwnershipMismatchException;
import com.example.marketplace.product.exception.ProductReadinessException;
import com.example.marketplace.product.exception.ProductReadinessFailure;
import com.example.marketplace.product_variant.EProductVariantStatus;
import com.example.marketplace.product_variant.ProductVariant;
import com.example.marketplace.product_variant_version.EProductVariantVersionStatus;
import com.example.marketplace.product_variant_version.ProductVariantVersion;
import com.example.marketplace.product_version.EProductVersionStatus;
import com.example.marketplace.product_version.ProductVersion;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ProductPublicationLifecycleTest {

    private static final UUID STORE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID PRODUCT_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID VERSION_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");
    private static final UUID CATEGORY_ID = UUID.fromString("00000000-0000-0000-0000-000000000004");
    private static final UUID SMALL_VARIANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000005");
    private static final UUID MEDIUM_VARIANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000006");
    private static final UUID OTHER_PRODUCT_ID = UUID.fromString("00000000-0000-0000-0000-000000000007");
    private static final UUID OTHER_VERSION_ID = UUID.fromString("00000000-0000-0000-0000-000000000008");
    private static final UUID SMALL_OFFER_ID = UUID.fromString("00000000-0000-0000-0000-000000000009");
    private static final UUID MEDIUM_OFFER_ID = UUID.fromString("00000000-0000-0000-0000-000000000010");
    private static final UUID EXTRA_VARIANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000011");
    private static final UUID EXTRA_OFFER_ID = UUID.fromString("00000000-0000-0000-0000-000000000012");
    private static final Currency USD = Currency.getInstance("USD");

    @Test
    void ac01_createProductRetainsStoreOwnershipWithoutPublicationState() {
        Product product = Product.create(PRODUCT_ID, STORE_ID, "P1");

        assertThat(product.getId()).isEqualTo(PRODUCT_ID);
        assertThat(product.getStoreId()).isEqualTo(STORE_ID);
        assertThat(product.getProductCode()).isEqualTo("P1");
        assertThat(product.getCurrentPublishedVersionId()).isEmpty();
    }

    @Test
    void ac02_createFirstProductVersionStartsInDraft() {
        ProductVersion version = ProductVersion.create(VERSION_ID, PRODUCT_ID, 1);

        assertThat(version.getId()).isEqualTo(VERSION_ID);
        assertThat(version.getProductId()).isEqualTo(PRODUCT_ID);
        assertThat(version.getVersionNumber()).isEqualTo(1);
        assertThat(version.getPublicationStatus()).isEqualTo(EProductVersionStatus.DRAFT);
    }

    @Test
    void ac03_editVersionedDetailsDoesNotChangeProductIdentity() {
        Product product = Product.create(PRODUCT_ID, STORE_ID, "P1");
        ProductVersion version = ProductVersion.create(VERSION_ID, PRODUCT_ID, 1);

        version.updateDetails(
                "Black T-Shirt",
                "A soft cotton t-shirt",
                CATEGORY_ID,
                List.of("media://front", "media://back"));

        assertThat(version.getName()).isEqualTo("Black T-Shirt");
        assertThat(version.getDescription()).isEqualTo("A soft cotton t-shirt");
        assertThat(version.getCategoryId()).isEqualTo(CATEGORY_ID);
        assertThat(version.getMediaReferences()).containsExactly("media://front", "media://back");
        assertThat(version.getPublicationStatus()).isEqualTo(EProductVersionStatus.DRAFT);
        assertThat(product.getId()).isEqualTo(PRODUCT_ID);
        assertThat(product.getStoreId()).isEqualTo(STORE_ID);
        assertThat(product.getProductCode()).isEqualTo("P1");
        assertThat(product.getCurrentPublishedVersionId()).isEmpty();
    }

    @Test
    void ac04_stableVariantIdentityIsSeparatedFromVersionedOfferData() {
        ProductVariant variant = ProductVariant.create(SMALL_VARIANT_ID, PRODUCT_ID, "BLACK-S");
        ProductVariantVersion offer = ProductVariantVersion.create(
                SMALL_OFFER_ID,
                VERSION_ID,
                SMALL_VARIANT_ID,
                "BLACK-S",
                "Black / Small",
                money("100.00"),
                1);

        assertThat(variant.getProductId()).isEqualTo(PRODUCT_ID);
        assertThat(variant.getVariantCode()).isEqualTo("BLACK-S");
        assertThat(variant.getStatus()).isEqualTo(EProductVariantStatus.ACTIVE);
        assertThat(offer.getProductVersionId()).isEqualTo(VERSION_ID);
        assertThat(offer.getProductVariantId()).isEqualTo(SMALL_VARIANT_ID);
        assertThat(offer.getSku()).isEqualTo("BLACK-S");
        assertThat(offer.getDisplayName()).isEqualTo("Black / Small");
        assertThat(offer.getPrice()).isEqualTo(money("100.00"));
        assertThat(offer.getPublicationStatus()).isEqualTo(EProductVariantVersionStatus.DRAFT);
    }

    @ParameterizedTest
    @MethodSource("validInitialQuantities")
    void ac05_initializeInventoryDoesNotChangeVariantOrVersionLifecycle(int availableQuantity) {
        Product product = Product.create(PRODUCT_ID, STORE_ID, "P1");
        ProductVariant variant = ProductVariant.create(SMALL_VARIANT_ID, PRODUCT_ID, "BLACK-S");
        ProductVersion version = completeDraftVersion();

        Inventory inventory = variant.initializeInventory(availableQuantity);

        assertThat(inventory.getProductVariantId()).isEqualTo(SMALL_VARIANT_ID);
        assertThat(inventory.getAvailableQuantity()).isEqualTo(availableQuantity);
        assertThat(variant.getStatus()).isEqualTo(EProductVariantStatus.ACTIVE);
        assertThat(version.getPublicationStatus()).isEqualTo(EProductVersionStatus.DRAFT);
        assertThat(product.getCurrentPublishedVersionId()).isEmpty();
    }

    @Test
    void ac05_negativeInitialInventoryIsRejected() {
        ProductVariant variant = ProductVariant.create(SMALL_VARIANT_ID, PRODUCT_ID, "BLACK-S");

        assertThatThrownBy(() -> variant.initializeInventory(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("availableQuantity");
    }

    @Test
    void ac06_secondInventoryForTheSameVariantIsRejected() {
        ProductVariant variant = ProductVariant.create(SMALL_VARIANT_ID, PRODUCT_ID, "BLACK-S");
        Inventory originalInventory = variant.initializeInventory(20);

        assertThatThrownBy(() -> variant.initializeInventory(15))
                .isInstanceOf(DuplicateInventoryException.class)
                .hasMessageContaining(SMALL_VARIANT_ID.toString());

        assertThat(originalInventory.getProductVariantId()).isEqualTo(SMALL_VARIANT_ID);
        assertThat(originalInventory.getAvailableQuantity()).isEqualTo(20);
    }

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
        assertThat(offers)
                .extracting(ProductVariantVersion::getPublicationStatus)
                .containsOnly(EProductVariantVersionStatus.DRAFT);
    }

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
        assertThat(product.getCurrentPublishedVersionId()).contains(VERSION_ID);
    }

    @Test
    void ac10_submitOutsideDraftIsRejectedWithoutMutation() {
        ProductVersion version = completeDraftVersion();
        List<ProductVariantVersion> offers = validOffers();
        version.submitForReview(activeVariants(), offers);

        assertThatThrownBy(() -> version.submitForReview(activeVariants(), offers))
                .isInstanceOf(IllegalLifecycleTransitionException.class)
                .hasMessageContaining("IN_REVIEW")
                .hasMessageContaining("submit");

        assertThat(version.getPublicationStatus()).isEqualTo(EProductVersionStatus.IN_REVIEW);
        assertThat(offers)
                .extracting(ProductVariantVersion::getPublicationStatus)
                .containsOnly(EProductVariantVersionStatus.IN_REVIEW);
    }

    @Test
    void ac10_publishOutsideInReviewIsRejectedWithoutMutation() {
        Product product = Product.create(PRODUCT_ID, STORE_ID, "P1");
        ProductVersion version = completeDraftVersion();
        List<ProductVariantVersion> offers = validOffers();

        assertThatThrownBy(() -> product.publish(version, offers))
                .isInstanceOf(IllegalLifecycleTransitionException.class)
                .hasMessageContaining("DRAFT")
                .hasMessageContaining("publish");

        assertThat(version.getPublicationStatus()).isEqualTo(EProductVersionStatus.DRAFT);
        assertThat(offers)
                .extracting(ProductVariantVersion::getPublicationStatus)
                .containsOnly(EProductVariantVersionStatus.DRAFT);
        assertThat(product.getCurrentPublishedVersionId()).isEmpty();
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
                .hasMessageContaining("PUBLISHED")
                .hasMessageContaining("submit");

        assertThat(version.getPublicationStatus()).isEqualTo(EProductVersionStatus.PUBLISHED);
        assertThat(offers)
                .extracting(ProductVariantVersion::getPublicationStatus)
                .containsOnly(EProductVariantVersionStatus.PUBLISHED);
        assertThat(product.getCurrentPublishedVersionId()).contains(VERSION_ID);
    }

    @Test
    void ac10_submitRejectsChildOutsideDraftWithoutMutation() {
        ProductVersion reviewedVersion = completeDraftVersion();
        List<ProductVariantVersion> offers = validOffers();
        reviewedVersion.submitForReview(activeVariants(), offers);
        ProductVersion draftVersion = completeDraftVersion();

        assertThatThrownBy(() -> draftVersion.submitForReview(activeVariants(), offers))
                .isInstanceOf(IllegalLifecycleTransitionException.class)
                .hasMessageContaining("IN_REVIEW")
                .hasMessageContaining("submit");

        assertThat(draftVersion.getPublicationStatus()).isEqualTo(EProductVersionStatus.DRAFT);
        assertThat(offers)
                .extracting(ProductVariantVersion::getPublicationStatus)
                .containsOnly(EProductVariantVersionStatus.IN_REVIEW);
    }

    @Test
    void ac10_repeatedPublishIsRejectedWithoutMutation() {
        Product product = Product.create(PRODUCT_ID, STORE_ID, "P1");
        ProductVersion version = completeDraftVersion();
        List<ProductVariantVersion> offers = validOffers();
        version.submitForReview(activeVariants(), offers);
        product.publish(version, offers);

        assertThatThrownBy(() -> product.publish(version, offers))
                .isInstanceOf(IllegalLifecycleTransitionException.class)
                .hasMessageContaining("PUBLISHED")
                .hasMessageContaining("publish");

        assertThat(version.getPublicationStatus()).isEqualTo(EProductVersionStatus.PUBLISHED);
        assertThat(offers)
                .extracting(ProductVariantVersion::getPublicationStatus)
                .containsOnly(EProductVariantVersionStatus.PUBLISHED);
        assertThat(product.getCurrentPublishedVersionId()).contains(VERSION_ID);
    }

    @ParameterizedTest(name = "AC-11 rejects ownership mismatch: {0}")
    @MethodSource("ownershipMismatches")
    void ac11_mismatchedOwnershipIsRejected(String description, Runnable invalidAssociation) {
        assertThatThrownBy(invalidAssociation::run)
                .isInstanceOf(OwnershipMismatchException.class);
    }

    @Test
    void ac12_failedChildPublicationLeavesTheWholeAggregateUnchanged() {
        Product product = Product.create(PRODUCT_ID, STORE_ID, "P1");
        ProductVersion version = completeDraftVersion();
        List<ProductVariantVersion> offers = new ArrayList<>(validOffers());
        version.submitForReview(activeVariants(), offers);
        offers.add(ProductVariantVersion.create(
                EXTRA_OFFER_ID,
                VERSION_ID,
                EXTRA_VARIANT_ID,
                "BLACK-L",
                "Black / Large",
                money("120.00"),
                3));

        assertThatThrownBy(() -> product.publish(version, offers))
                .isInstanceOf(IllegalLifecycleTransitionException.class);

        assertThat(version.getPublicationStatus()).isEqualTo(EProductVersionStatus.IN_REVIEW);
        assertThat(offers.get(0).getPublicationStatus()).isEqualTo(EProductVariantVersionStatus.IN_REVIEW);
        assertThat(offers.get(1).getPublicationStatus()).isEqualTo(EProductVariantVersionStatus.IN_REVIEW);
        assertThat(offers.get(2).getPublicationStatus()).isEqualTo(EProductVariantVersionStatus.DRAFT);
        assertThat(product.getCurrentPublishedVersionId()).isEmpty();
    }

    private static Stream<Integer> validInitialQuantities() {
        return Stream.of(0, 20);
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

    private static Stream<Arguments> ownershipMismatches() {
        return Stream.of(
                Arguments.of(
                        "product version belongs to another product",
                        (Runnable) () -> Product.create(PRODUCT_ID, STORE_ID, "P1")
                                .publish(ProductVersion.create(VERSION_ID, OTHER_PRODUCT_ID, 1), List.of())),
                Arguments.of(
                        "variant belongs to another product",
                        (Runnable) () -> completeDraftVersion().submitForReview(
                                List.of(ProductVariant.create(SMALL_VARIANT_ID, OTHER_PRODUCT_ID, "BLACK-S")),
                                List.of(validOffer(SMALL_VARIANT_ID, "BLACK-S", "100.00", 1)))),
                Arguments.of(
                        "offer belongs to another product version",
                        (Runnable) () -> completeDraftVersion().submitForReview(
                                activeVariants(),
                                List.of(ProductVariantVersion.create(
                                        SMALL_OFFER_ID,
                                        OTHER_VERSION_ID,
                                        SMALL_VARIANT_ID,
                                        "BLACK-S",
                                        "Black / Small",
                                        money("100.00"),
                                        1)))),
                Arguments.of(
                        "offer references a variant outside the submitted aggregate",
                        (Runnable) () -> completeDraftVersion().submitForReview(
                                activeVariants(),
                                List.of(validOffer(EXTRA_VARIANT_ID, "BLACK-L", "120.00", 3)))));
    }

    private static ProductVersion completeDraftVersion() {
        return draftVersion("Black T-Shirt", "A soft cotton t-shirt", CATEGORY_ID);
    }

    private static ProductVersion draftVersion(String name, String description, UUID categoryId) {
        ProductVersion version = ProductVersion.create(VERSION_ID, PRODUCT_ID, 1);
        version.updateDetails(name, description, categoryId, List.of("media://front"));
        return version;
    }

    private static List<ProductVariant> activeVariants() {
        return List.of(
                ProductVariant.create(SMALL_VARIANT_ID, PRODUCT_ID, "BLACK-S"),
                ProductVariant.create(MEDIUM_VARIANT_ID, PRODUCT_ID, "BLACK-M"));
    }

    private static ProductVariant discontinuedVariant() {
        ProductVariant variant = ProductVariant.create(SMALL_VARIANT_ID, PRODUCT_ID, "BLACK-S");
        variant.discontinue();
        return variant;
    }

    private static List<ProductVariantVersion> validOffers() {
        return List.of(
                validOffer(SMALL_VARIANT_ID, "BLACK-S", "100.00", 1),
                validOffer(MEDIUM_VARIANT_ID, "BLACK-M", "110.00", 2));
    }

    private static ProductVariantVersion validOffer(
            UUID variantId,
            String sku,
            String amount,
            int sortOrder) {
        return offerWithPrice(variantId, sku, money(amount), sortOrder);
    }

    private static ProductVariantVersion offerWithPrice(
            UUID variantId,
            String sku,
            Money price,
            int sortOrder) {
        return ProductVariantVersion.create(
                sortOrder == 1 ? SMALL_OFFER_ID : MEDIUM_OFFER_ID,
                VERSION_ID,
                variantId,
                sku,
                sku,
                price,
                sortOrder);
    }

    private static Money money(String amount) {
        return new Money(new BigDecimal(amount), USD);
    }
}
