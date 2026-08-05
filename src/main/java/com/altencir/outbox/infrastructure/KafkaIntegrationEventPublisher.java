package com.altencir.outbox.infrastructure;

import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class KafkaIntegrationEventPublisher implements IntegrationEventPublisher {
    @Inject
    @Channel("payment-events")
    Emitter<Record<String, String>> emitter;

    @Inject
    FailureSwitch failureSwitch;

    @Override
    public CompletionStage<Void> publish(String idempotencyKey, String payload) {
        if (failureSwitch.consume()) {
            return CompletableFuture.failedStage(new IllegalStateException("Controlled Kafka publication failure."));
        }
        return emitter.send(Record.of(idempotencyKey, payload));
    }
}
