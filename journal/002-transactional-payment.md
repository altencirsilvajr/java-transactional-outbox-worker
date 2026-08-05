# 002 - Pagamento transacional e contrato HTTP

## Commit

`feat: persist payment with transactional outbox`

## Objetivo

Autorizar um pagamento por HTTP e expor pagamento e mensagem outbox criados na mesma transação PostgreSQL.

## Implementacao

- Projeto Java 21/Quarkus 3.33.3 com Gradle Wrapper 8.14.3.
- Migração Flyway para pagamentos, outbox, lease e evidências consumidas.
- Endpoint Jakarta REST com chave idempotente, validação e Problem Details.
- Escrita atômica via Jakarta Transactions e entidades Hibernate ORM/Panache.
- Snapshot operacional sem expor entidades de persistência.
- Testes HTTP contra PostgreSQL efêmero iniciado pelo Quarkus Dev Services/Testcontainers.

## Rastreabilidade ADR

`ADR aplicado: ADR-0001 - PostgreSQL como fronteira atômica do outbox.`

## Verificacao

- `JAVA_HOME=/opt/homebrew/opt/openjdk@21 ./gradlew test --rerun-tasks --console=plain`: o primeiro red retornou 404 nos três cenários ainda sem recurso HTTP.
- `JAVA_HOME=/opt/homebrew/opt/openjdk@21 ./gradlew test --console=plain`: passou com os testes HTTP após corrigir a equivalência `CHAR`/`VARCHAR` detectada pela validação de schema.
- `./scripts/verify-traceability.sh --staged`: passou com exatamente um Journal.
- `git diff --check --cached`: passou sem erros.

## Alternativas e trade-offs

O payload permanece `TEXT` no PostgreSQL para manter o evento opaco ao banco; validação e versionamento pertencem ao contrato Kafka.

## Proximo passo

Adicionar claim concorrente, publicação Kafka, retry e consumidor idempotente.
