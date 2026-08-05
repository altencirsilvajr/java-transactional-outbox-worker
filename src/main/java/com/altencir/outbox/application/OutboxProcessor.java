package com.altencir.outbox.application;

import com.altencir.outbox.infrastructure.IntegrationEventPublisher;
import com.altencir.outbox.infrastructure.OutboxStore;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.jboss.logging.Logger;

@ApplicationScoped
public class OutboxProcessor {
    private static final Logger LOG = Logger.getLogger(OutboxProcessor.class);
    private final String owner = "worker-" + UUID.randomUUID();

    @Inject
    OutboxStore store;

    @Inject
    IntegrationEventPublisher publisher;

    @Inject
    WorkerConfiguration configuration;

    public void processAvailable() {
        var now = Instant.now();
        var claimed = store.claim(
                configuration.batchSize(), owner, now, Duration.ofSeconds(configuration.leaseSeconds()));
        for (var message : claimed) {
            try {
                publisher.publish(message.idempotencyKey(), message.payload()).toCompletableFuture().join();
                store.markPublished(message.id(), owner, Instant.now());
                LOG.infov("Outbox message {0} published with key {1}", message.id(), message.idempotencyKey());
            } catch (RuntimeException exception) {
                var cause = exception.getCause() == null ? exception : exception.getCause();
                store.recordFailure(
                        message.id(), owner, Instant.now(), configuration.maxAttempts(), cause.getMessage());
                LOG.warnv("Outbox message {0} will retry after publication failure: {1}", message.id(), cause.getMessage());
            }
        }
    }

    @ConfigMapping(prefix = "outbox.worker")
    public interface WorkerConfiguration {
        @WithDefault("20")
        int batchSize();

        @WithDefault("30")
        int leaseSeconds();

        @WithDefault("5")
        int maxAttempts();

        @WithDefault("1s")
        String interval();
    }
}
