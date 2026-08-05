# ADR-0003 - Angular como console local de aprendizagem

## Status

Aceito

## Contexto

Os estados intermediários do outbox, especialmente backoff e consumo idempotente, são difíceis de demonstrar apenas com logs e `curl`.

## Decisão

Manter um console Angular 22.1 mínimo que consome exclusivamente a API real. O backend expõe uma falha controlada de próxima publicação para tornar retry e recuperação reproduzíveis.

## Consequências

- O fluxo pode ser explicado visualmente em uma entrevista.
- A UI não contém regra de pagamentos, outbox ou retry.
- O endpoint de falha é uma ferramenta explícita do laboratório e não faria parte de uma API produtiva.

## Alternativas rejeitadas

### Dashboard estático com dados simulados

Não comprovaria o contrato nem os estados reais do PostgreSQL e Kafka.
