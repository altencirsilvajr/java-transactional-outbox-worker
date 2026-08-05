# 004 - Superfície de demonstração e entrega

## Commit

`feat: complete local outbox laboratory`

## Objetivo

Entregar o laboratório demonstrável do zero com Angular real, contêineres, automação e documentação operacional.

## Implementacao

- Angular 22.1 standalone com formulário, estados operacionais e falha controlada pela API real.
- Imagens multi-stage sem root e Compose com PostgreSQL 16.8, Redpanda/Kafka, API e Nginx.
- GitHub Actions, Jenkinsfile e GitLab CI com testes backend/frontend e gates.
- Manifesto OpenShift com Deployment, Service, ConfigMap, Secret externo e Route.
- Contrato JSON Schema versionado, correlação HTTP, logs JSON, health, OpenAPI e métricas.
- README, runbook e guia honesto de modernização/convivência com legados.
- Auditor de histórico que exige um Journal por commit substantivo.

## Rastreabilidade ADR

`Novo ADR criado: ADR-0003 - Angular como console local de aprendizagem.`

## Verificacao

- `JAVA_HOME=/opt/homebrew/opt/openjdk@21 ./gradlew test --console=plain`: passou com 6 testes e PostgreSQL/Kafka via Testcontainers.
- `PATH=/opt/homebrew/opt/node@24/bin:$PATH npm --prefix frontend ci`: instalou a árvore reproduzível pelo lockfile.
- `PATH=/opt/homebrew/opt/node@24/bin:$PATH npm --prefix frontend run test:ci`: passou com 1 teste Angular.
- `PATH=/opt/homebrew/opt/node@24/bin:$PATH npm --prefix frontend run build`: passou; bundle inicial de 290,42 kB.
- `PATH=/opt/homebrew/opt/node@24/bin:$PATH npm --prefix frontend audit --omit=dev`: 0 vulnerabilidades de runtime; o audit completo reportou 3 moderadas em tooling de desenvolvimento.
- `docker compose config --quiet`: passou.
- `docker compose up --build -d`: construiu as duas imagens e iniciou os quatro serviços; PostgreSQL e Kafka ficaram saudáveis.
- Smoke pelo proxy `http://localhost:8088`: criou pagamento, preservou `X-Correlation-ID`, publicou no Kafka e exibiu uma evidência consumida; `/q/health` retornou `UP` e a métrica de publicação chegou a 1.
- Parser YAML: leu os 4 recursos do manifesto OpenShift.
- `./scripts/audit-history.sh`: todos os commits publicados tinham exatamente um Journal.
- `docker compose down`: removeu contêineres e rede, preservando o volume nomeado.
- `./scripts/verify-traceability.sh --staged` e `git diff --check --cached`: passaram antes do commit.

## Alternativas e trade-offs

Redpanda oferece Kafka compatível em um contêiner pequeno para demonstração local; os contratos SmallRye permanecem compatíveis com um cluster Kafka gerenciado.

## Proximo passo

Nenhum; o laboratório está pronto para demonstração e entrevistas.
