package com.altencir.outbox.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_messages")
public class OutboxMessageEntity extends PanacheEntityBase {
    @Id
    public UUID id;

    @Column(name = "aggregate_id", nullable = false)
    public UUID aggregateId;

    @Column(name = "event_type", nullable = false, length = 160)
    public String eventType;

    @Column(nullable = false, columnDefinition = "text")
    public String payload;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 180)
    public String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    public OutboxStatus status;

    @Column(name = "attempt_count", nullable = false)
    public int attemptCount;

    @Column(name = "next_attempt_at", nullable = false)
    public Instant nextAttemptAt;

    @Column(name = "lease_owner", length = 120)
    public String leaseOwner;

    @Column(name = "leased_until")
    public Instant leasedUntil;

    @Column(name = "published_at")
    public Instant publishedAt;

    @Column(name = "last_error", length = 1000)
    public String lastError;

    @Version
    @Column(nullable = false)
    public long version;
}
