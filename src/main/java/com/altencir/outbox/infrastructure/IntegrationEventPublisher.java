package com.altencir.outbox.infrastructure;

import java.util.concurrent.CompletionStage;

public interface IntegrationEventPublisher {
    CompletionStage<Void> publish(String idempotencyKey, String payload);
}
