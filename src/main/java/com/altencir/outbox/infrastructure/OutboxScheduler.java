package com.altencir.outbox.infrastructure;

import com.altencir.outbox.application.OutboxProcessor;
import io.quarkus.scheduler.Scheduled;
import io.quarkus.runtime.ShutdownEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@ApplicationScoped
public class OutboxScheduler {
    private boolean stopping;

    @Inject
    OutboxProcessor processor;

    @Scheduled(every = "${outbox.worker.interval}", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    synchronized void publishPendingMessages() {
        if (!stopping) {
            processor.processAvailable();
        }
    }

    synchronized void stop(@Observes ShutdownEvent event) {
        stopping = true;
    }
}
