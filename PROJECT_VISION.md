# Visão do projeto

## Propósito

Construir um laboratório vertical Java 21 com Quarkus que demonstre como persistir um pagamento e seu evento na mesma transação e publicá-lo no Kafka com entrega pelo menos uma vez.

## Pressão estudada

Banco e broker não compartilham uma transação. O worker pode competir com outras instâncias, falhar antes ou depois da publicação e reenviar mensagens. A solução precisa tornar essas janelas explícitas, recuperáveis e observáveis.

## Critérios de sucesso

- Pagamento e outbox persistidos atomicamente no PostgreSQL.
- Claim concorrente com lease e reprocessamento após expiração.
- Publicação Kafka at-least-once com retry/backoff e dead-letter operacional.
- Chave idempotente no evento e consumidor de evidência deduplicado.
- API REST, OpenAPI, Problem Details, health, métricas e logs correlacionados.
- Angular mínimo consumindo apenas a API real.
- Execução local reproduzível com Docker Compose.

## Fora de escopo

- Processamento real de cartão, exactly-once distribuído ou ambiente de produção.
- Compatibilidade binária com o laboratório .NET.
- Cluster Kubernetes/OpenShift obrigatório para demonstração local.
