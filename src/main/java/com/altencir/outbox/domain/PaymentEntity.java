package com.altencir.outbox.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class PaymentEntity extends PanacheEntityBase {
    @Id
    public UUID id;

    @Column(name = "request_idempotency_key", nullable = false, unique = true, length = 120)
    public String idempotencyKey;

    @Column(nullable = false, precision = 18, scale = 2)
    public BigDecimal amount;

    @Column(nullable = false, length = 3)
    public String currency;

    @Column(name = "authorized_at", nullable = false)
    public Instant authorizedAt;
}
