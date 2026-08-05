CREATE TABLE payments (
    id UUID PRIMARY KEY,
    request_idempotency_key VARCHAR(120) NOT NULL UNIQUE,
    amount NUMERIC(18, 2) NOT NULL CHECK (amount > 0),
    currency VARCHAR(3) NOT NULL,
    authorized_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE outbox_messages (
    id UUID PRIMARY KEY,
    aggregate_id UUID NOT NULL REFERENCES payments(id),
    event_type VARCHAR(160) NOT NULL,
    payload TEXT NOT NULL,
    idempotency_key VARCHAR(180) NOT NULL UNIQUE,
    status VARCHAR(24) NOT NULL,
    attempt_count INTEGER NOT NULL DEFAULT 0,
    next_attempt_at TIMESTAMP WITH TIME ZONE NOT NULL,
    lease_owner VARCHAR(120),
    leased_until TIMESTAMP WITH TIME ZONE,
    published_at TIMESTAMP WITH TIME ZONE,
    last_error VARCHAR(1000),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX ix_outbox_ready ON outbox_messages(status, next_attempt_at, leased_until);

CREATE TABLE consumed_events (
    idempotency_key VARCHAR(180) PRIMARY KEY,
    event_type VARCHAR(160) NOT NULL,
    consumed_at TIMESTAMP WITH TIME ZONE NOT NULL
);
