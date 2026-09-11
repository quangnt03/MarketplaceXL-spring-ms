package com.example.marketplace.product;

import static com.example.marketplace.product.ProductLifecycleFixtures.PRODUCT_ID;
import static com.example.marketplace.product.ProductLifecycleFixtures.STORE_ID;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.marketplace.product_version.EProductVersionStatus;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

/**
 * Product identity lifecycle. Covers AC-01.
 * See artifacts/product-publication-lifecycle/specification.md.
 */
class ProductTest {

    @Test
    void ac01_createProductRetainsStoreOwnershipWithoutPublicationState() {
        Product product = Product.create(PRODUCT_ID, STORE_ID, "P1");

        assertThat(product.getId()).isEqualTo(PRODUCT_ID);
        assertThat(product.getStoreId()).isEqualTo(STORE_ID);
        assertThat(product.getProductCode()).isEqualTo("P1");
        assertThat(product.getCurrentPublishedVersionId()).isNull();
    }
}
