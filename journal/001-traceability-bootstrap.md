# 001 - Bootstrap de rastreabilidade

## Commit

`chore: bootstrap tracked development`

## Objetivo

Estabelecer visão, limites, seams e gates auditáveis antes da implementação do laboratório.

## Implementacao

- Visão e SDD ativo com os quatro incrementos verticais.
- Regras locais e processo de desenvolvimento rastreável.
- Gate executável que exige exatamente um Journal e declaração de ADR.
- Decisão durável sobre a fronteira transacional do outbox.

## Rastreabilidade ADR

`Novo ADR criado: ADR-0001 - PostgreSQL como fronteira atômica do outbox.`

## Verificacao

- `./scripts/verify-traceability.sh --staged`: passou com exatamente um Journal.
- `git diff --check --cached`: passou sem erros.

## Alternativas e trade-offs

O processo fica no próprio repositório para permanecer verificável sem ferramenta externa.

## Proximo passo

Criar test-first o contrato HTTP e a escrita atômica de pagamento e outbox.
