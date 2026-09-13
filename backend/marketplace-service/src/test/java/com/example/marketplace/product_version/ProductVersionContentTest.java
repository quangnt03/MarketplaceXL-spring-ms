package com.example.marketplace.product_version;

import static com.example.marketplace.product.ProductLifecycleFixtures.CATEGORY_ID;
import static com.example.marketplace.product.ProductLifecycleFixtures.PRODUCT_ID;
import static com.example.marketplace.product.ProductLifecycleFixtures.STORE_ID;
import static com.example.marketplace.product.ProductLifecycleFixtures.VERSION_ID;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.marketplace.product.Product;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * ProductVersion creation and draft-content editing. Covers AC-02, AC-03.
 * See artifacts/product-publication-lifecycle/specification.md.
 */
class ProductVersionContentTest {

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
        assertThat(product.getCurrentPublishedVersionId()).isNull();
    }
}
