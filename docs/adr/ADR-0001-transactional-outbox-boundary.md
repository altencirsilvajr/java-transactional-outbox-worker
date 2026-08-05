# ADR-0001 - PostgreSQL como fronteira atômica do outbox

## Status

Aceito

## Contexto

Uma autorização precisa criar o pagamento e a intenção de integração sem depender de uma transação distribuída com Kafka.

## Decisão

Pagamento e mensagem outbox serão inseridos na mesma transação PostgreSQL. A API nunca publicará diretamente no broker; um worker assíncrono fará isso depois do commit.

## Consequências

- Um commit bem-sucedido sempre deixa evidência durável para recuperação.
- A janela entre publicação e atualização do banco permite duplicatas; consumidores precisam ser idempotentes.
- A API responde sem aguardar Kafka.

## Alternativas rejeitadas

### Publicar no handler HTTP

Uma falha após o commit perderia o evento, e uma falha antes do commit poderia publicar um pagamento inexistente.
