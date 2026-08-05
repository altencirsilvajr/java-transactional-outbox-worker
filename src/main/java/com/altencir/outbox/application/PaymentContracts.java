package com.altencir.outbox.application;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class PaymentContracts {
    private PaymentContracts() {
    }

    public record CreatePaymentRequest(
            @DecimalMin(value = "0.01", message = "amount must be greater than zero") BigDecimal amount,
            @NotBlank(message = "currency is required") String currency) {
    }

    public record PaymentResponse(
            UUID paymentId,
            String idempotencyKey,
            BigDecimal amount,
            String currency,
            Instant authorizedAt,
            UUID outboxMessageId,
            String outboxStatus,
            boolean reused) {
    }

    public record PaymentView(
            UUID id, String idempotencyKey, BigDecimal amount, String currency, Instant authorizedAt) {
    }

    public record MessageView(
            UUID id,
            UUID aggregateId,
            String eventType,
            String payload,
            String idempotencyKey,
            String status,
            int attemptCount,
            Instant nextAttemptAt,
            Instant leasedUntil,
            Instant publishedAt,
            String lastError) {
    }

    public record ConsumedEventView(String idempotencyKey, String eventType, Instant consumedAt) {
    }

    public record OperationsSnapshot(
            List<PaymentView> payments, List<MessageView> messages, List<ConsumedEventView> consumedEvents) {
    }
}
