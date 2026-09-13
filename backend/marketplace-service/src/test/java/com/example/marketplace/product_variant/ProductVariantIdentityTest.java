package com.example.marketplace.product_variant;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import static com.example.marketplace.product.ProductLifecycleFixtures.PRODUCT_ID;
import static com.example.marketplace.product.ProductLifecycleFixtures.SMALL_OFFER_ID;
import static com.example.marketplace.product.ProductLifecycleFixtures.SMALL_SKU;
import static com.example.marketplace.product.ProductLifecycleFixtures.SMALL_VARIANT_ID;
import static com.example.marketplace.product.ProductLifecycleFixtures.VERSION_ID;
import static com.example.marketplace.product.ProductLifecycleFixtures.money;
import com.example.marketplace.product_variant_version.EProductVariantVersionStatus;
import com.example.marketplace.product_variant_version.ProductVariantVersion;

/**
 * Stable ProductVariant identity separated from versioned ProductVariantVersion offer data.
 * Covers AC-04. See artifacts/product-publication-lifecycle/specification.md.
 */
class ProductVariantIdentityTest {

    @Test
    void ac04_stableVariantIdentityIsSeparatedFromVersionedOfferData() {
        ProductVariant variant = ProductVariant.create(SMALL_VARIANT_ID, PRODUCT_ID, SMALL_SKU);
        ProductVariantVersion offer = ProductVariantVersion.create(
                SMALL_OFFER_ID,
                "Black / Small",
                money("100.00"),
                SMALL_VARIANT_ID,
                VERSION_ID,
                SMALL_SKU,
                1);

        assertThat(variant.getProductId()).isEqualTo(PRODUCT_ID);
        assertThat(variant.getVariantCode()).isEqualTo(SMALL_SKU);
        assertThat(variant.getStatus()).isEqualTo(EProductVariantStatus.ACTIVE);
        assertThat(offer.getProductVersionId()).isEqualTo(VERSION_ID);
        assertThat(offer.getProductVariantId()).isEqualTo(SMALL_VARIANT_ID);
        assertThat(offer.getSku()).isEqualTo(SMALL_SKU);
        assertThat(offer.getDisplayName()).isEqualTo("Black / Small");
        assertThat(offer.getPrice().amount()).isEqualTo(money("100.00").amount());
        assertThat(offer.getPrice().currency()).isEqualTo(money("100.00").currency());
        assertThat(offer.getPublicationStatus()).isEqualTo(EProductVariantVersionStatus.DRAFT);
    }
}
