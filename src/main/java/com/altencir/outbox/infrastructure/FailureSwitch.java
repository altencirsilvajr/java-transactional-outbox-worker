package com.altencir.outbox.infrastructure;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.concurrent.atomic.AtomicBoolean;

@ApplicationScoped
public class FailureSwitch {
    private final AtomicBoolean failNext = new AtomicBoolean();

    public void arm() {
        failNext.set(true);
    }

    public boolean consume() {
        return failNext.compareAndSet(true, false);
    }
}
