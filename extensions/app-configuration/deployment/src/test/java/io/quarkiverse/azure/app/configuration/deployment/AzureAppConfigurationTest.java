package io.quarkiverse.azure.app.configuration.deployment;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;
import io.quarkus.test.common.QuarkusTestResource;
import io.smallrye.config.SmallRyeConfig;

@QuarkusTestResource(AzureAppConfigurationResource.class)
class AzureAppConfigurationTest {
    @RegisterExtension
    static final QuarkusUnitTest TEST = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class))
            .overrideConfigKey("quarkus.azure.app.configuration.endpoint", "http://localhost:8082")
            .overrideConfigKey("quarkus.azure.app.configuration.id", "dummy")
            .overrideConfigKey("quarkus.azure.app.configuration.secret", "aGVsbG8=");

    @Inject
    SmallRyeConfig config;

    @Test
    void azureAppConfiguration() {
        assertEquals("1234", config.getRawValue("my.prop"));
        assertEquals("5678", config.getRawValue("another.prop"));
    }
}
