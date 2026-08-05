# Runbook operacional

## Sinais

- `GET /q/health/ready`: disponibilidade do PostgreSQL e do canal Kafka.
- `GET /q/metrics`: contadores `outbox_publications_total` com resultados `published`, `retry` e `failed`.
- `GET /api/operations/snapshot`: idade, status, tentativas, lease e último erro por mensagem.
- Logs JSON em produção: procure `correlationId`, ID da mensagem e chave idempotente.

## Diagnóstico

1. Se `PENDING` cresce, confirme saúde do Kafka e compare `nextAttemptAt` com o relógio UTC.
2. Se `CLAIMED` envelhece, confirme se `leasedUntil` expirou; outro worker poderá recuperar o item.
3. Se `FAILED` cresce, leia `lastError`, corrija a dependência e reenvie somente por procedimento controlado.
4. Se há duplicatas no tópico, confirme que o consumidor usa a chave Kafka como chave da deduplicação antes de aplicar efeitos.

## Rollout e rollback

Execute Flyway antes de aumentar réplicas e mantenha consumidores compatíveis com `PaymentAuthorized.v1`. O schema inicial é aditivo. Para rollback da aplicação, restaure a imagem anterior sem reverter tabelas. Nunca apague outbox pendente durante rollback.

## Segredos

O manifesto referencia `outbox-api-secrets`; crie-o fora do Git com `DB_USER` e `DB_PASSWORD`. ConfigMaps guardam somente endereços e configuração não sensível.
