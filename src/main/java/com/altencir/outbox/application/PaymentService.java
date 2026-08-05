package com.altencir.outbox.application;

import com.altencir.outbox.application.PaymentContracts.CreatePaymentRequest;
import com.altencir.outbox.application.PaymentContracts.PaymentResponse;
import com.altencir.outbox.domain.OutboxMessageEntity;
import com.altencir.outbox.domain.OutboxStatus;
import com.altencir.outbox.domain.PaymentEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@ApplicationScoped
public class PaymentService {
    private static final String EVENT_TYPE = "PaymentAuthorized.v1";

    @Inject
    ObjectMapper objectMapper;

    private final Clock clock = Clock.systemUTC();

    @Transactional
    public AuthorizationResult authorize(String requestKey, CreatePaymentRequest request) {
        var normalizedKey = validateKey(requestKey);
        var amount = request.amount() == null ? null : request.amount().setScale(2, RoundingMode.UNNECESSARY);
        var currency = normalizeCurrency(request.currency());
        if (amount == null || amount.signum() <= 0) {
            throw new DomainValidationException("Payment amount must be greater than zero.");
        }

        var existing = PaymentEntity.<PaymentEntity>find("idempotencyKey", normalizedKey).firstResult();
        if (existing != null) {
            if (existing.amount.compareTo(amount) != 0 || !existing.currency.equals(currency)) {
                throw new IdempotencyConflictException("Idempotency key was already used with a different payment.");
            }
            var message = OutboxMessageEntity.<OutboxMessageEntity>find("aggregateId", existing.id).singleResult();
            return new AuthorizationResult(toResponse(existing, message, true), false);
        }

        var now = Instant.now(clock);
        var payment = new PaymentEntity();
        payment.id = UUID.randomUUID();
        payment.idempotencyKey = normalizedKey;
        payment.amount = amount;
        payment.currency = currency;
        payment.authorizedAt = now;

        var message = new OutboxMessageEntity();
        message.id = UUID.randomUUID();
        message.aggregateId = payment.id;
        message.eventType = EVENT_TYPE;
        message.idempotencyKey = "payment-authorized:" + normalizedKey;
        message.status = OutboxStatus.PENDING;
        message.attemptCount = 0;
        message.nextAttemptAt = now;
        message.payload = serialize(new PaymentAuthorizedEvent(
                "1", payment.id, payment.amount, payment.currency, payment.authorizedAt, message.idempotencyKey));

        payment.persist();
        message.persist();
        return new AuthorizationResult(toResponse(payment, message, false), true);
    }

    private String validateKey(String key) {
        if (key == null || key.isBlank() || key.length() > 120) {
            throw new DomainValidationException("Idempotency-Key is required and must have at most 120 characters.");
        }
        return key.trim();
    }

    private String normalizeCurrency(String currency) {
        if (currency == null || !currency.matches("[A-Za-z]{3}")) {
            throw new DomainValidationException("Currency must be a three-letter ISO code.");
        }
        return currency.toUpperCase(Locale.ROOT);
    }

    private String serialize(PaymentAuthorizedEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Could not serialize integration event.", exception);
        }
    }

    private PaymentResponse toResponse(PaymentEntity payment, OutboxMessageEntity message, boolean reused) {
        return new PaymentResponse(
                payment.id,
                payment.idempotencyKey,
                payment.amount,
                payment.currency,
                payment.authorizedAt,
                message.id,
                message.status.name(),
                reused);
    }

    public record AuthorizationResult(PaymentResponse response, boolean created) {
    }

    public record PaymentAuthorizedEvent(
            String schemaVersion,
            UUID paymentId,
            java.math.BigDecimal amount,
            String currency,
            Instant authorizedAt,
            String idempotencyKey) {
    }
}
