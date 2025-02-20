package io.quarkus.reactive.oracle.client;

import java.util.concurrent.CompletionStage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;
import io.vertx.sqlclient.Pool;

public class OraclePoolProducerTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withConfigurationResource("application-default-datasource.properties")
            .withApplicationRoot((jar) -> jar
                    .addClasses(BeanUsingBareOracleClient.class)
                    .addClasses(BeanUsingMutinyOracleClient.class));

    @Inject
    BeanUsingBareOracleClient beanUsingBare;

    @Inject
    BeanUsingMutinyOracleClient beanUsingMutiny;

    @Test
    public void testVertxInjection() {
        beanUsingBare.verify()
                .thenCompose(v -> beanUsingMutiny.verify())
                .toCompletableFuture()
                .join();
    }

    @ApplicationScoped
    static class BeanUsingBareOracleClient {

        @Inject
        Pool oracleClient;

        public CompletionStage<?> verify() {
            return oracleClient.query("SELECT 1 FROM DUAL").execute().toCompletionStage();
        }
    }

    @ApplicationScoped
    static class BeanUsingMutinyOracleClient {

        @Inject
        io.vertx.mutiny.sqlclient.Pool oracleClient;

        public CompletionStage<Void> verify() {
            return oracleClient.query("SELECT 1 FROM DUAL").execute()
                    .onItem().ignore().andContinueWithNull()
                    .subscribeAsCompletionStage();
        }
    }
}
