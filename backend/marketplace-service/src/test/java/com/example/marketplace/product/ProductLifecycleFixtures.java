package com.example.marketplace.product;

import com.example.marketplace.money.Money;
import com.example.marketplace.product_variant.ProductVariant;
import com.example.marketplace.product_variant_version.ProductVariantVersion;
import com.example.marketplace.product_version.ProductVersion;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

/**
 * Shared fixtures for the product-publication-lifecycle domain test suite.
 * See artifacts/product-publication-lifecycle/specification.md for the acceptance criteria these back.
 */
public final class ProductLifecycleFixtures {

    public static final UUID STORE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    public static final UUID PRODUCT_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    public static final UUID VERSION_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");
    public static final UUID CATEGORY_ID = UUID.fromString("00000000-0000-0000-0000-000000000004");
    public static final UUID SMALL_VARIANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000005");
    public static final UUID MEDIUM_VARIANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000006");
    public static final UUID OTHER_PRODUCT_ID = UUID.fromString("00000000-0000-0000-0000-000000000007");
    public static final UUID OTHER_VERSION_ID = UUID.fromString("00000000-0000-0000-0000-000000000008");
    public static final UUID SMALL_OFFER_ID = UUID.fromString("00000000-0000-0000-0000-000000000009");
    public static final UUID MEDIUM_OFFER_ID = UUID.fromString("00000000-0000-0000-0000-000000000010");
    public static final UUID EXTRA_VARIANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000011");
    public static final UUID EXTRA_OFFER_ID = UUID.fromString("00000000-0000-0000-0000-000000000012");
    public static final Currency USD = Currency.getInstance("USD");
    public static final String SMALL_SKU = "BLACK-S";
    public static final String MEDIUM_SKU = "BLACK-M";

    private ProductLifecycleFixtures() { }

    public static Money money(String amount) {
        return new Money(new BigDecimal(amount), USD);
    }

    public static ProductVersion completeDraftVersion() {
        return draftVersion("Black T-Shirt", "A soft cotton t-shirt", CATEGORY_ID);
    }

    public static ProductVersion draftVersion(String name, String description, UUID categoryId) {
        ProductVersion version = ProductVersion.create(VERSION_ID, PRODUCT_ID, 1);
        version.updateDetails(name, description, categoryId, List.of("media://front"));
        return version;
    }

    public static List<ProductVariant> activeVariants() {
        return List.of(
                ProductVariant.create(SMALL_VARIANT_ID, PRODUCT_ID, SMALL_SKU),
                ProductVariant.create(MEDIUM_VARIANT_ID, PRODUCT_ID, MEDIUM_SKU));
    }

    public static ProductVariant discontinuedVariant() {
        ProductVariant variant = ProductVariant.create(SMALL_VARIANT_ID, PRODUCT_ID, SMALL_SKU);
        variant.discontinue();
        return variant;
    }

    public static List<ProductVariantVersion> validOffers() {
        return List.of(
                validOffer(SMALL_VARIANT_ID, SMALL_SKU, "100.00", 1),
                validOffer(MEDIUM_VARIANT_ID, MEDIUM_SKU, "110.00", 2));
    }

    public static ProductVariantVersion validOffer(UUID variantId, String sku, String amount, int sortOrder) {
        return offerWithPrice(variantId, sku, money(amount), sortOrder);
    }

    public static ProductVariantVersion offerWithPrice(UUID variantId, String sku, Money price, int sortOrder) {
        return ProductVariantVersion.create(
                sortOrder == 1 ? SMALL_OFFER_ID : MEDIUM_OFFER_ID,
                sku,
                price,
                variantId,
                VERSION_ID,
                sku,
                sortOrder);
    }

    /**
     * Builds an offer with an explicit, possibly mismatched, productVersionId for ownership tests.
     * Uses the real {@link ProductVariantVersion#create} parameter order to avoid the
     * transposed-argument mistake the old inline calls made.
     */
    public static ProductVariantVersion offerForVersion(
            UUID id,
            UUID productVersionId,
            UUID productVariantId,
            String sku,
            String displayName,
            Money price,
            int sortOrder) {
        return ProductVariantVersion.create(id, displayName, price, productVariantId, productVersionId, sku, sortOrder);
    }
}
