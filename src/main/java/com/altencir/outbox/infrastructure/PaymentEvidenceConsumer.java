package com.altencir.outbox.infrastructure;

import com.altencir.outbox.domain.ConsumedEventEntity;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.Instant;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class PaymentEvidenceConsumer {
    private static final Logger LOG = Logger.getLogger(PaymentEvidenceConsumer.class);

    @Incoming("payment-events-evidence")
    @Transactional
    public void consume(Record<String, String> record) {
        if (ConsumedEventEntity.findById(record.key()) != null) {
            LOG.infov("Duplicate event ignored for key {0}", record.key());
            return;
        }
        var evidence = new ConsumedEventEntity();
        evidence.idempotencyKey = record.key();
        evidence.eventType = "PaymentAuthorized.v1";
        evidence.consumedAt = Instant.now();
        evidence.persist();
    }
}
