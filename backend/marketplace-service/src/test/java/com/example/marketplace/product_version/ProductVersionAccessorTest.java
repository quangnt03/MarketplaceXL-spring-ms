package com.example.marketplace.product_version;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProductVersionAccessorTest {

    @Test
    void settersUpdateValuesExposedByGetters() {
        UUID initialId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID initialProductId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        UUID updatedId = UUID.fromString("00000000-0000-0000-0000-000000000003");
        UUID updatedProductId = UUID.fromString("00000000-0000-0000-0000-000000000004");
        UUID categoryId = UUID.fromString("00000000-0000-0000-0000-000000000005");
        List<String> mediaReferences = List.of("media://front", "media://back");
        ProductVersion version = ProductVersion.create(initialId, initialProductId, 1);

        version.setId(updatedId);
        version.setProductId(updatedProductId);
        version.setCategoryId(categoryId);
        version.setVersionNumber(2);
        version.setProductName("Black T-Shirt");
        version.setDescription("A soft cotton t-shirt");
        version.setMediaReferences(mediaReferences);

        assertThat(version.getId()).isEqualTo(updatedId);
        assertThat(version.getProductId()).isEqualTo(updatedProductId);
        assertThat(version.getCategoryId()).isEqualTo(categoryId);
        assertThat(version.getVersionNumber()).isEqualTo(2);
        assertThat(version.getName()).isEqualTo("Black T-Shirt");
        assertThat(version.getDescription()).isEqualTo("A soft cotton t-shirt");
        assertThat(version.getMediaReferences()).isEqualTo(mediaReferences);
        assertThat(version.getPublicationStatus()).isEqualTo(EProductVersionStatus.DRAFT);
    }
}
