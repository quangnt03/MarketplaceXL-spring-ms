package com.example.marketplace.product;

import static com.example.marketplace.product.ProductLifecycleFixtures.EXTRA_VARIANT_ID;
import static com.example.marketplace.product.ProductLifecycleFixtures.OTHER_PRODUCT_ID;
import static com.example.marketplace.product.ProductLifecycleFixtures.OTHER_VERSION_ID;
import static com.example.marketplace.product.ProductLifecycleFixtures.PRODUCT_ID;
import static com.example.marketplace.product.ProductLifecycleFixtures.SMALL_OFFER_ID;
import static com.example.marketplace.product.ProductLifecycleFixtures.SMALL_SKU;
import static com.example.marketplace.product.ProductLifecycleFixtures.SMALL_VARIANT_ID;
import static com.example.marketplace.product.ProductLifecycleFixtures.STORE_ID;
import static com.example.marketplace.product.ProductLifecycleFixtures.VERSION_ID;
import static com.example.marketplace.product.ProductLifecycleFixtures.activeVariants;
import static com.example.marketplace.product.ProductLifecycleFixtures.completeDraftVersion;
import static com.example.marketplace.product.ProductLifecycleFixtures.money;
import static com.example.marketplace.product.ProductLifecycleFixtures.offerForVersion;
import static com.example.marketplace.product.ProductLifecycleFixtures.validOffer;
import static com.example.marketplace.product.ProductLifecycleFixtures.validOffers;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.marketplace.shared.exception.OwnershipMismatchException;
import com.example.marketplace.product_variant.ProductVariant;
import com.example.marketplace.product_variant_version.ProductVariantVersion;
import com.example.marketplace.product_version.ProductVersion;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Ownership checks across Product, ProductVersion, ProductVariant, and ProductVariantVersion. Covers AC-11.
 * See artifacts/product-publication-lifecycle/specification.md.
 */
class ProductOwnershipTest {

    @Test
    @DisplayName("AC-11 rejects: product version belongs to another product")
    void ac11_publishRejectsVersionBelongingToAnotherProduct() {
        Product product = Product.create(PRODUCT_ID, STORE_ID, "P1");
        ProductVersion version = ProductVersion.create(VERSION_ID, OTHER_PRODUCT_ID, 1);

        assertThatThrownBy(() -> product.publish(version, List.of()))
                .isInstanceOf(OwnershipMismatchException.class);
    }

    @Test
    @DisplayName("AC-11 rejects: variant belongs to another product")
    void ac11_submitRejectsVariantBelongingToAnotherProduct() {
        ProductVersion version = completeDraftVersion();
        List<ProductVariant> variants = List.of(ProductVariant.create(SMALL_VARIANT_ID, OTHER_PRODUCT_ID, SMALL_SKU));
        List<ProductVariantVersion> offers = List.of(validOffer(SMALL_VARIANT_ID, SMALL_SKU, "100.00", 1));

        assertThatThrownBy(() -> version.submitForReview(variants, offers))
                .isInstanceOf(OwnershipMismatchException.class);
    }

    @Test
    @DisplayName("AC-11 rejects: offer belongs to another product version")
    void ac11_submitRejectsOfferBelongingToAnotherProductVersion() {
        ProductVersion version = completeDraftVersion();
        List<ProductVariantVersion> offers = List.of(offerForVersion(
                SMALL_OFFER_ID,
                OTHER_VERSION_ID,
                SMALL_VARIANT_ID,
                SMALL_SKU,
                "Black / Small",
                money("100.00"),
                1));

        assertThatThrownBy(() -> version.submitForReview(activeVariants(), offers))
                .isInstanceOf(OwnershipMismatchException.class);
    }

    @Test
    @DisplayName("AC-11 rejects: offer references a variant outside the submitted aggregate")
    void ac11_submitRejectsOfferReferencingVariantOutsideSubmittedAggregate() {
        ProductVersion version = completeDraftVersion();
        List<ProductVariantVersion> offers = List.of(validOffer(EXTRA_VARIANT_ID, "BLACK-L", "120.00", 3));

        assertThatThrownBy(() -> version.submitForReview(activeVariants(), offers))
                .isInstanceOf(OwnershipMismatchException.class);
    }

    @Test
    @DisplayName("AC-11 rejects: publish rejects an offer belonging to another product version")
    void ac11_publishRejectsOfferBelongingToAnotherProductVersion() {
        Product product = Product.create(PRODUCT_ID, STORE_ID, "P1");
        ProductVersion version = completeDraftVersion();
        List<ProductVariantVersion> offers = new ArrayList<>(validOffers());
        version.submitForReview(activeVariants(), offers);
        offers.set(
                0,
                offerForVersion(
                        SMALL_OFFER_ID,
                        OTHER_VERSION_ID,
                        SMALL_VARIANT_ID,
                        SMALL_SKU,
                        "Black / Small",
                        money("100.00"),
                        1));

        assertThatThrownBy(() -> product.publish(version, offers))
                .isInstanceOf(OwnershipMismatchException.class);
    }
}
