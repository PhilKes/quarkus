package io.quarkus.opentelemetry.deployment;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.opentelemetry.deployment.common.exporter.InMemoryExporter;
import io.quarkus.opentelemetry.runtime.tracing.Traceless;
import io.quarkus.test.QuarkusUnitTest;

public class OpenTelemetryTracelessWithoutPathTest {
    @RegisterExtension
    static final QuarkusUnitTest TEST = new QuarkusUnitTest().setArchiveProducer(
            () -> ShrinkWrap.create(JavaArchive.class)
                    .addPackage(InMemoryExporter.class.getPackage())
                    .addAsResource(new StringAsset(
                            ""), "application.properties")
                    .addClasses(TracelessWithoutPath.class))
            .assertException(throwable -> {
                assertThat(throwable).isInstanceOf(IllegalStateException.class)
                        .hasMessageContaining("The class")
                        .hasMessageContaining(
                                "is annotated with @Traceless but is missing the required @Path annotation. Please ensure that the class is properly annotated with @Path annotation.");
            });

    @Test
    void testClassAnnotatedWithTracelessMissingPath() {
        Assertions.fail();
    }

    @Traceless
    public static class TracelessWithoutPath {

    }
}
