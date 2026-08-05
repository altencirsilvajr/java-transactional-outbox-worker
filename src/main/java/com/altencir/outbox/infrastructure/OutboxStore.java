package com.altencir.outbox.infrastructure;

import com.altencir.outbox.application.ClaimedMessage;
import com.altencir.outbox.domain.OutboxMessageEntity;
import com.altencir.outbox.domain.OutboxStatus;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class OutboxStore {
    @Inject
    EntityManager entityManager;

    @Inject
    MeterRegistry meterRegistry;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<ClaimedMessage> claim(int batchSize, String owner, Instant now, Duration leaseDuration) {
        @SuppressWarnings("unchecked")
        var messages = (List<OutboxMessageEntity>) entityManager.createNativeQuery("""
                        SELECT * FROM outbox_messages
                        WHERE (status = 'PENDING' AND next_attempt_at <= :now)
                           OR (status = 'CLAIMED' AND leased_until <= :now)
                        ORDER BY next_attempt_at
                        FOR UPDATE SKIP LOCKED
                        LIMIT :batchSize
                        """, OutboxMessageEntity.class)
                .setParameter("now", now)
                .setParameter("batchSize", batchSize)
                .getResultList();

        return messages.stream().map(message -> {
            message.status = OutboxStatus.CLAIMED;
            message.leaseOwner = owner;
            message.leasedUntil = now.plus(leaseDuration);
            return new ClaimedMessage(message.id, message.eventType, message.payload, message.idempotencyKey);
        }).toList();
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean markPublished(UUID id, String owner, Instant now) {
        var message = OutboxMessageEntity.<OutboxMessageEntity>findById(id);
        if (!ownsLease(message, owner)) {
            return false;
        }
        message.attemptCount++;
        message.status = OutboxStatus.PUBLISHED;
        message.publishedAt = now;
        message.leaseOwner = null;
        message.leasedUntil = null;
        message.lastError = null;
        meterRegistry.counter("outbox.publications", "result", "published").increment();
        return true;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean recordFailure(UUID id, String owner, Instant now, int maxAttempts, String error) {
        var message = OutboxMessageEntity.<OutboxMessageEntity>findById(id);
        if (!ownsLease(message, owner)) {
            return false;
        }
        message.attemptCount++;
        message.lastError = truncate(error);
        message.leaseOwner = null;
        message.leasedUntil = null;
        if (message.attemptCount >= maxAttempts) {
            message.status = OutboxStatus.FAILED;
            meterRegistry.counter("outbox.publications", "result", "failed").increment();
        } else {
            message.status = OutboxStatus.PENDING;
            message.nextAttemptAt = now.plusSeconds(Math.min(60, 1L << message.attemptCount));
            meterRegistry.counter("outbox.publications", "result", "retry").increment();
        }
        return true;
    }

    private boolean ownsLease(OutboxMessageEntity message, String owner) {
        return message != null && message.status == OutboxStatus.CLAIMED && owner.equals(message.leaseOwner);
    }

    private String truncate(String error) {
        var safe = error == null || error.isBlank() ? "Unknown publication failure." : error;
        return safe.substring(0, Math.min(safe.length(), 1000));
    }
}
