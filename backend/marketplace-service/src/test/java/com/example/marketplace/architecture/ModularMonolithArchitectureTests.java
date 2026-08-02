package com.example.marketplace.architecture;

import com.example.marketplace.MarketplaceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularMonolithArchitectureTests {

    @Test
    void modulesRespectDeclaredBoundaries() {
        ApplicationModules.of(MarketplaceApplication.class).verify();
    }
}
