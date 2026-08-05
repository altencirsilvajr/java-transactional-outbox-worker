# 005 - Publicação do CI no GitHub Actions

## Commit

`ci: publish GitHub Actions workflow`

## Objetivo

Ativar o workflow preservado no incremento local sem modificar o histórico já publicado.

## Implementacao

- Publica gates de testes Gradle, testes e build Angular, validação do Compose e auditoria do histórico.
- Publica workflow e Journal no mesmo commit remoto atômico.
- Mantém uma safety branch com o commit local original anterior à reconstrução.

## Rastreabilidade ADR

Decisao local sem ADR novo: o incremento ativa automação de entrega já definida.

## Verificacao

- O incremento de produto reconstruído foi publicado por fast-forward normal sem o workflow.
- A árvore reconstruída difere da safety branch somente pela ausência temporária do workflow.
- Atualização remota feita sem force push.
- Execução do GitHub Actions verificada após a publicação.

## Alternativas e trade-offs

A GitHub App foi usada para contornar somente a limitação de escopo `workflow` do token Git local.

## Proximo passo

Tratar o workflow como gate obrigatório para alterações no worker e no console.
