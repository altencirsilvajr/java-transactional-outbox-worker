# 007 - Reparar lockfile do frontend

## Commit

`fix: synchronize frontend lockfiles`

## Objetivo

Restaurar o lockfile completo do console do worker.

## Implementacao

- Recupera a copia integral preservada e regenera seu metadata com npm 11.17.

## Rastreabilidade ADR

Decisao local sem ADR novo: reparo operacional sem alterar a entrega do outbox.

## Verificacao

- Lockfile JSON valido; `npm ci` sem warnings.
- Audit: 0 vulnerabilidades; nenhum script pendente.
