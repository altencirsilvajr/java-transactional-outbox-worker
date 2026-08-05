package com.altencir.outbox.application;

import com.altencir.outbox.application.PaymentContracts.ConsumedEventView;
import com.altencir.outbox.application.PaymentContracts.MessageView;
import com.altencir.outbox.application.PaymentContracts.OperationsSnapshot;
import com.altencir.outbox.application.PaymentContracts.PaymentView;
import com.altencir.outbox.domain.ConsumedEventEntity;
import com.altencir.outbox.domain.OutboxMessageEntity;
import com.altencir.outbox.domain.PaymentEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class OperationsQuery {

    @Transactional(Transactional.TxType.SUPPORTS)
    public OperationsSnapshot snapshot() {
        var payments = PaymentEntity.<PaymentEntity>list("order by authorizedAt desc").stream()
                .map(payment -> new PaymentView(
                        payment.id, payment.idempotencyKey, payment.amount, payment.currency, payment.authorizedAt))
                .toList();
        var messages = OutboxMessageEntity.<OutboxMessageEntity>list("order by nextAttemptAt desc").stream()
                .map(message -> new MessageView(
                        message.id,
                        message.aggregateId,
                        message.eventType,
                        message.payload,
                        message.idempotencyKey,
                        message.status.name(),
                        message.attemptCount,
                        message.nextAttemptAt,
                        message.leasedUntil,
                        message.publishedAt,
                        message.lastError))
                .toList();
        var consumedEvents = ConsumedEventEntity.<ConsumedEventEntity>list("order by consumedAt desc").stream()
                .map(event -> new ConsumedEventView(event.idempotencyKey, event.eventType, event.consumedAt))
                .toList();
        return new OperationsSnapshot(payments, messages, consumedEvents);
    }
}
