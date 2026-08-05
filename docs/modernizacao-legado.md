# Contexto de modernização e convivência

Este laboratório representa um recorte comum de modernização: o sistema legado continua autorizando ou consultando dados enquanto um novo fluxo Java publica fatos de integração. Kafka funciona como ponte durante a convivência; o outbox impede que uma transação confirmada desapareça da integração.

Em uma migração de Java 8, JBoss EAP/WebLogic, JSF/PrimeFaces/RichFaces ou Delphi, a primeira etapa seria caracterizar regras e contratos existentes com testes. O novo serviço não deveria importar entidades, sessões HTTP ou transações do legado. REST/SOAP podem servir como anti-corruption layer temporária; eventos versionados transportam fatos já confirmados.

SVN pode coexistir durante a transição, mas o histórico deste laboratório usa Git. Jenkins e GitLab CI estão representados por pipelines equivalentes ao GitHub Actions. O manifesto OpenShift usa Deployment, ConfigMap, Secret, Service e Route sem versionar credenciais.

O projeto não afirma experiência operacional real com Delphi, WebLogic ou JBoss: esses itens são contexto documentado, enquanto Quarkus, PostgreSQL, Kafka, Docker e Angular são executáveis.
