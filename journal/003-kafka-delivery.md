# 003 - Entrega Kafka at-least-once

## Commit

`feat: deliver outbox events at least once`

## Objetivo

Publicar mensagens pendentes no Kafka com claim concorrente, retry observável e evidência de consumo idempotente.

## Implementacao

- Scheduler Quarkus com lote e prevenção de execução concorrente local.
- Claim PostgreSQL por `FOR UPDATE SKIP LOCKED`, dono e lease recuperável.
- Publisher SmallRye Kafka com chave idempotente e confirmação assíncrona.
- Retry com backoff exponencial limitado e estado terminal `FAILED` configurável.
- Consumidor Kafka que persiste uma única evidência por chave idempotente.
- Métricas de publicação e endpoint controlado para demonstrar uma falha transitória.
- Logs estruturados com IDs da mensagem e chave de integração.

## Rastreabilidade ADR

`Novo ADR criado: ADR-0002 - Entrega at-least-once com claim por lease.`

## Verificacao

- `JAVA_HOME=/opt/homebrew/opt/openjdk@21 ./gradlew test --tests '*OutboxDeliveryTest' --console=plain`: o red manteve a mensagem em `PENDING` porque ainda não havia producer nem consumer conectados.
- `JAVA_HOME=/opt/homebrew/opt/openjdk@21 ./gradlew test --console=plain`: passou com 6 testes, usando PostgreSQL e Kafka efêmeros via Testcontainers; cobriu publicação, consumo, falha transitória, backoff e recuperação.
- `./scripts/verify-traceability.sh --staged`: passou com exatamente um Journal.
- `git diff --check --cached`: passou sem erros.

## Alternativas e trade-offs

O claim fica no PostgreSQL porque a tabela outbox já é a fonte de recuperação; um coordenador distribuído adicional aumentaria a superfície sem remover duplicatas.

## Proximo passo

Entregar console Angular, Compose, CI, manifests e documentação de operação.
