# PS_Empresa

Microsserviço responsável pelo domínio de empresas do My Pet Admin.

## Estado atual

- Java 25 LTS
- Spring Boot 4.1.1
- PostgreSQL + Flyway
- Spring Security
- Swagger/OpenAPI
- Actuator/Prometheus
- Docker + GitHub Actions

## Cadastro interno

O contrato interno legado permanece disponível:

- `POST /internal/empresas`

Para o fluxo coordenado pelo PS_Orchestrator existe o contrato idempotente:

- `POST /internal/empresas/onboarding`
- header obrigatório `X-Onboarding-Id: <UUID>`
- body: mesmo `EmpresaRequestDTO` do cadastro atual

### Idempotência de onboarding

O PS_Empresa persiste `onboarding_id` e um hash SHA-256 do payload normalizado.

Regras:

- primeiro uso do `onboardingId` cria a empresa;
- replay com o mesmo `onboardingId` e o mesmo payload retorna a empresa já criada;
- reutilização da mesma chave com payload diferente retorna `409 ONBOARDING_CONFLICT`;
- lock transacional PostgreSQL serializa requisições concorrentes com a mesma chave;
- índice único protege a invariável no banco;
- empresas criadas fora do fluxo de onboarding permanecem com os campos técnicos nulos.

O endpoint público e os contratos internos existentes não mudam de comportamento.
