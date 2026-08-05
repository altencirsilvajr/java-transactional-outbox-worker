package com.altencir.outbox.application;

import java.util.UUID;

public record ClaimedMessage(UUID id, String eventType, String payload, String idempotencyKey) {
}
