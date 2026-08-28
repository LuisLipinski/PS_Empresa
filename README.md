# 🐾 My Pet Admin — PS_Empresa

Microsserviço responsável pelo **domínio de empresas (petshops)** do My Pet Admin.

O PS_Empresa mantém os dados cadastrais e o status operacional da empresa. Ele **não orquestra** criação de usuário, contrato ou login. Essas coordenações pertencem a uma camada externa de orquestração.

## Responsabilidades

- cadastrar uma empresa;
- consultar empresas com filtros, paginação e ordenação;
- consultar empresa por ID;
- atualizar parcialmente dados cadastrais;
- excluir uma empresa;
- manter o status da empresa;
- receber do PS_Contrato alterações de status;
- disponibilizar contrato interno mínimo para consulta do status da empresa.

## Regra de status

Toda nova empresa nasce como:

`AGUARDANDO_CONTRATO`

O PS_Empresa não ativa a empresa durante o cadastro. O status é atualizado somente a partir de informações recebidas do PS_Contrato.

| Status recebido do contrato | Status da empresa |
| --- | --- |
| `ATIVO` | `ATIVO` |
| `AGUARDANDO_PAGAMENTO` | `AGUARDANDO_CONTRATO` |
| `PENDENTE_PAGAMENTO` | mantém o status atual; se já estiver `ATIVO`, permanece `ATIVO` |
| `INATIVO` | `INATIVO` |

## Arquitetura de integração

### Onboarding

O frontend não deve coordenar microsserviços diretamente.

Fluxo recomendado:

```text
Frontend
   |
   v
API Gateway
   |
   v
Onboarding Orchestrator
   |----> PS_Empresa
   |----> PS_User
   `----> PS_Contrato
```

O **API Gateway** cuida de entrada, autenticação, roteamento e políticas transversais. O **Orchestrator** cuida do workflow de negócio do onboarding e da composição das respostas dos microsserviços.

Para o onboarding coordenado, o cadastro idempotente da empresa é feito em:

`POST /internal/empresas/onboarding`

com o header obrigatório:

`X-Onboarding-Id: <UUID>`

O contrato interno anterior `POST /internal/empresas` permanece disponível por compatibilidade, mas não oferece a semântica de replay por `onboardingId`.

#### Idempotência do onboarding

O PS_Empresa persiste o `onboardingId` e um hash SHA-256 do payload normalizado para proteger retries do primeiro passo do fluxo.

Regras:

- o primeiro uso de um `onboardingId` cria a empresa;
- replay com o mesmo `onboardingId` e o mesmo payload retorna a empresa já criada;
- reutilização do mesmo `onboardingId` com payload diferente retorna `409 ONBOARDING_CONFLICT`;
- um lock transacional PostgreSQL serializa requisições concorrentes com a mesma chave;
- um índice único parcial protege a invariável de unicidade no banco;
- empresas criadas fora do fluxo idempotente permanecem sem `onboardingId` técnico.

### Login e vínculo usuário → empresa

O PS_Empresa não deve armazenar a relação de pertencimento do usuário apenas para resolver login.

Fluxo recomendado:

```text
PS_Login / Login Orchestrator
        |
        | userId
        v
     PS_User
        |
        | empresaId
        v
   PS_Empresa
        |
        | status
        v
 decisão de login
```

O PS_User resolve a empresa à qual o usuário pertence. O PS_Empresa responde pelo estado da empresa por meio de:

`GET /internal/empresas/{empresaId}/status`

Assim cada microsserviço continua dono do seu próprio domínio.

## Segurança

### APIs internas e chamadas service-to-service

A credencial interna é enviada no header:

`X-Internal-Key`

O valor vem da variável de ambiente:

`INTERNAL_API_KEY`

Rotas `/internal/**` exigem obrigatoriamente essa credencial. Rotas autenticadas `/empresas/**` também aceitam a credencial interna para chamadas confiáveis de gateway/orchestrator e para testes de integração isolados do PS_Login.

Nenhum segredo de produção é mantido no código-fonte.

### APIs de usuário autenticado

Rotas `/empresas/**` aceitam JWT no header:

`Authorization: Bearer <token>`

Nesta versão o PS_Empresa valida os tokens HS256 atualmente emitidos pelo PS_Login usando:

`JWT_SECRET_KEY`

O contrato atual do PS_Login contém `subject` e `roles`. A inclusão de `userId`/`empresaId` no token deve ser tratada em uma evolução coordenada do fluxo de autenticação.

> **Importante para multi-tenant:** autenticação JWT sozinha não resolve isolamento entre empresas. Antes da comercialização, o fluxo de autorização deve garantir que o usuário só consiga acessar a empresa associada a ele. A solução recomendada é o PS_User resolver o vínculo `userId -> empresaId` e o gateway/orchestrator propagar somente chamadas autorizadas, ou evoluir o token para carregar um `empresaId` confiável e validado.

## Endpoints

### Internos

| Método | Endpoint | Responsabilidade |
| --- | --- | --- |
| POST | `/internal/empresas` | cadastro interno legado/compatibilidade |
| POST | `/internal/empresas/onboarding` | cadastrar empresa de forma idempotente no onboarding (`X-Onboarding-Id`) |
| GET | `/internal/empresas/{id}/status` | consultar somente o status |
| PATCH | `/internal/contratos/status` | sincronizar status vindo do PS_Contrato |

### Autenticados

| Método | Endpoint | Responsabilidade |
| --- | --- | --- |
| GET | `/empresas` | listar/filtrar empresas |
| GET | `/empresas/{id}` | buscar por ID |
| PATCH | `/empresas/{id}` | atualização parcial |
| DELETE | `/empresas/{id}` | excluir empresa |

## Banco de dados

PostgreSQL é o banco de produção.

O schema é versionado com **Flyway** em:

`src/main/resources/db/migration`

Em produção o Hibernate usa:

`spring.jpa.hibernate.ddl-auto=validate`

Hibernate valida o schema; alterações estruturais devem entrar como migrations.

## Observabilidade

- Spring Boot Actuator;
- health probes;
- Micrometer/Prometheus;
- logs em stdout, adequados para containers;
- `X-Correlation-Id` propagado/gerado por requisição;
- logs de negócio evitam registrar e-mail, telefone e endereço completos.

## Stack

- Java 21
- Spring Boot 3.5.x
- Spring Web
- Spring Security
- Spring Data JPA
- Bean Validation
- PostgreSQL
- Flyway
- OpenAPI / Swagger
- Micrometer / Prometheus
- JUnit 5 / MockMvc / Mockito
- Maven
- Docker

## Testes e qualidade

A suíte possui testes de controller, service, mappers, helpers, validação de CNPJ, tratamento de exceções e filtros de segurança.

JaCoCo executa no `verify` e aplica quality gate de cobertura de linhas.

```bash
./mvnw clean verify
```

Além dos unitários, o CI sobe PostgreSQL real e executa a regressão Playwright do `ps_automacao_backend` contra a aplicação iniciada localmente.

## Execução local

Variáveis principais:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
INTERNAL_API_KEY
JWT_SECRET_KEY
```

Executar:

```bash
./mvnw spring-boot:run
```

Swagger:

`http://localhost:8081/swagger-ui/index.html`

Health:

`http://localhost:8081/actuator/health`

## Produção / Render

Ative explicitamente o profile de produção:

```text
SPRING_PROFILES_ACTIVE=prod
```

Configure no serviço:

```text
DB_URL=jdbc:postgresql://<host>:5432/<database>
DB_USERNAME=<database-user>
DB_PASSWORD=<database-password>
INTERNAL_API_KEY=<shared-internal-secret>
JWT_SECRET_KEY=<jwt-secret>
```

`PORT` é lido automaticamente pela aplicação através da variável fornecida pelo Render e não precisa ser fixado manualmente.

Enquanto a autenticação service-to-service por chave estiver em uso, `INTERNAL_API_KEY` deve possuir exatamente o mesmo valor no PS_Empresa e no PS_Contrato.

Para recursos hospedados no mesmo workspace e região do Render, prefira a rede privada para PostgreSQL e comunicação entre microsserviços.

Health check recomendado:

```text
/actuator/health
```
