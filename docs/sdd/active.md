# SDD ativo — Transactional Outbox Worker

## Resultado

Uma API autoriza pagamentos com idempotência, grava pagamento e evento outbox na mesma transação PostgreSQL e permite acompanhar a publicação Kafka até a evidência de consumo.

## Seams sob teste

- HTTP: criação idempotente e consulta do snapshot operacional.
- Persistência: atomicidade, claim exclusivo e recuperação de lease.
- Kafka: evento versionado e consumidor deduplicado por chave idempotente.

## Incrementos

1. Bootstrap de rastreabilidade e visão.
2. Contrato REST, schema PostgreSQL e escrita transacional test-first.
3. Worker concorrente, Kafka, retries e evidência idempotente.
4. Angular, contêineres, CI, manifests e documentação operacional.

Todos os incrementos foram concluídos e validados. Mudanças futuras devem abrir um novo SDD ou atualizar este resultado antes da implementação.

## Restrições

- Java 21, Quarkus 3.33 LTS e Gradle Wrapper.
- Código e commits em inglês; documentação em português brasileiro.
- Nenhum segredo real versionado.
