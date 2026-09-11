package com.example.marketplace.product_variant;

import static com.example.marketplace.product.ProductLifecycleFixtures.PRODUCT_ID;
import static com.example.marketplace.product.ProductLifecycleFixtures.SMALL_VARIANT_ID;
import static com.example.marketplace.product.ProductLifecycleFixtures.STORE_ID;
import static com.example.marketplace.product.ProductLifecycleFixtures.completeDraftVersion;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.marketplace.inventory.Inventory;
import com.example.marketplace.product.Product;
import com.example.marketplace.shared.exception.DuplicateInventoryException;
import com.example.marketplace.product_version.EProductVersionStatus;
import com.example.marketplace.product_version.ProductVersion;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Test;

/**
 * Inventory initialization against a stable ProductVariant, independent of publication lifecycle.
 * Covers AC-05, AC-06. See artifacts/product-publication-lifecycle/specification.md.
 */
class ProductVariantInventoryTest {

    @ParameterizedTest
    @MethodSource("validInitialQuantities")
    void ac05_initializeInventoryDoesNotChangeVariantOrVersionLifecycle(int availableQuantity) {
        Product product = Product.create(PRODUCT_ID, STORE_ID, "P1");
        ProductVariant variant = ProductVariant.create(SMALL_VARIANT_ID, PRODUCT_ID, "BLACK-S");
        ProductVersion version = completeDraftVersion();

        Inventory inventory = variant.initializeInventory(availableQuantity, 1);

        assertThat(inventory.getProductVariantId()).isEqualTo(SMALL_VARIANT_ID);
        assertThat(inventory.getAvailableQuantity()).isEqualTo(availableQuantity);
        assertThat(variant.getStatus()).isEqualTo(EProductVariantStatus.ACTIVE);
        assertThat(version.getPublicationStatus()).isEqualTo(EProductVersionStatus.DRAFT);
        assertThat(product.getCurrentPublishedVersionId()).isNull();
    }

    @Test
    void ac05_negativeInitialInventoryIsRejected() {
        ProductVariant variant = ProductVariant.create(SMALL_VARIANT_ID, PRODUCT_ID, "BLACK-S");

        assertThatThrownBy(() -> variant.initializeInventory(-1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("availableQuantity");
    }

    @Test
    void ac06_secondInventoryForTheSameVariantIsRejected() {
        ProductVariant variant = ProductVariant.create(SMALL_VARIANT_ID, PRODUCT_ID, "BLACK-S");
        Inventory originalInventory = variant.initializeInventory(20, 1);

        assertThatThrownBy(() -> variant.initializeInventory(15,1 ))
                .isInstanceOf(DuplicateInventoryException.class)
                .hasMessageContaining(SMALL_VARIANT_ID.toString());

        assertThat(originalInventory.getProductVariantId()).isEqualTo(SMALL_VARIANT_ID);
        assertThat(originalInventory.getAvailableQuantity()).isEqualTo(20);
    }

    private static Stream<Integer> validInitialQuantities() {
        return Stream.of(0, 20);
    }
}
