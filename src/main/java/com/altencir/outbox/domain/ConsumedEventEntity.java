package com.altencir.outbox.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "consumed_events")
public class ConsumedEventEntity extends PanacheEntityBase {
    @Id
    @Column(name = "idempotency_key", length = 180)
    public String idempotencyKey;

    @Column(name = "event_type", nullable = false, length = 160)
    public String eventType;

    @Column(name = "consumed_at", nullable = false)
    public Instant consumedAt;
}
