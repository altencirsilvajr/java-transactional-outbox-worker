# 006 - Endurecer toolchain de CI

## Commit

`ci: eliminate toolchain warnings`

## Objetivo

Eliminar alertas Angular e avisos de Actions baseadas em Node 20.

## Implementacao

- Fixa `@hono/node-server` corrigido em 2.1.0.
- Versiona a allowlist dos scripts de instalacao revisados.
- Atualiza Actions para Node 24 e adiciona audit ao pipeline.

## Rastreabilidade ADR

Decisao local sem ADR novo: manutencao operacional sem alterar o protocolo do outbox.

## Verificacao

- `npm audit`: 0 vulnerabilidades e nenhum script pendente.
- Teste frontend: 1 aprovado; build Angular aprovado.
- Workflow validado como YAML e sem Actions antigas.
