# ADR-0002 - Entrega at-least-once com claim por lease

## Status

Aceito

## Contexto

Mais de uma instância pode buscar o mesmo outbox. Além disso, o processo pode morrer depois de publicar no Kafka e antes de marcar a mensagem como publicada.

## Decisão

O worker seleciona lotes com `FOR UPDATE SKIP LOCKED`, marca cada item como `CLAIMED` com dono e expiração e confirma publicação em uma transação separada. Um lease expirado pode ser reclamado. A chave `payment-authorized:{requestKey}` é a chave Kafka, e o consumidor de evidência mantém uma tabela deduplicada por essa chave.

Falhas incrementam a tentativa, liberam o lease e aplicam backoff exponencial limitado. Após o limite configurado, a mensagem fica em `FAILED` para inspeção operacional.

## Consequências

- Workers concorrentes não processam simultaneamente o mesmo claim válido.
- Uma queda na janela Kafka/banco pode gerar uma duplicata, preservando at-least-once.
- O consumidor precisa confirmar a transação de deduplicação antes do ack.
- O banco coordena o trabalho; particionamento será necessário em volumes muito maiores.

## Alternativas rejeitadas

### Lock em memória

Não coordena réplicas nem sobrevive a reinícios.

### Exactly-once como promessa ponta a ponta

Transações Kafka não incluem o PostgreSQL da API e não eliminam a obrigação de idempotência nos efeitos externos.
