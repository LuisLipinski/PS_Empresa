# 🐾 My Pet Admin — PS_Empresa

Microsserviço responsável pelo **domínio de empresas (petshops)** do My Pet Admin.

O PS_Empresa mantém os dados cadastrais e o status operacional da empresa. Ele **não orquestra** criação de usuário, contrato, convite de ativação ou login. Essas coordenações pertencem a uma camada externa de orquestração.

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
   |----> PS_Contrato
   `----> PS_Login (convite de ativação após criação da identidade)
```

O **API Gateway** cuida de entrada, autenticação, roteamento e políticas transversais. O **Orchestrator** cuida do workflow de negócio do onboarding e da composição das respostas dos microsserviços.

O cadastro da empresa é feito internamente pelo orchestrator em:

`POST /internal/empresas`

A criação do primeiro MASTER ocorre no PS_User. Depois que a identidade existir, o orchestrator solicita ao PS_Login o convite de ativação. O PS_User não deve assumir responsabilidade por senha ou envio de e-mail de ativação.

### Login e vínculo usuário → empresa

O PS_Empresa não deve armazenar a relação de pertencimento do usuário apenas para resolver login.

Fluxo atual:

```text
Frontend
   |
   v
PS_Login
   |
   | email / userId
   v
PS_User
   |
   | empresaId + status + roles
   v
PS_Login
   |
   `--> access JWT
```

O PS_User resolve a empresa à qual o usuário pertence. O PS_Login é dono da autenticação e emite o JWT. O PS_Empresa continua dono exclusivamente do domínio Empresa.

O PS_Empresa disponibiliza o estado da empresa por meio de:

`GET /internal/empresas/{empresaId}/status`

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

Nesta versão o PS_Empresa valida os tokens HS256 emitidos pelo PS_Login usando:

`JWT_SECRET_KEY`

O contrato atual do access token contém:

- `sub = userId`;
- `empresaId`;
- `roles`;
- `iss`;
- `iat`;
- `exp`;
- `jti`.

Enquanto os microsserviços validarem HS256 diretamente, `JWT_SECRET_KEY` precisa ser coordenado com o PS_Login. A direção futura é centralizar a borda no API Gateway e migrar para assinatura assimétrica/JWKS.

> **Importante para multi-tenant:** autenticação JWT sozinha não resolve isolamento entre empresas. O `empresaId` do token fornece contexto confiável somente depois de validada a assinatura, mas cada operação de domínio ainda deve garantir que o recurso acessado pertence ao tenant autorizado. Essa autorização deve ser consolidada antes da comercialização SaaS.

## Endpoints

### Internos

| Método | Endpoint | Responsabilidade |
| --- | --- | --- |
| POST | `/internal/empresas` | cadastrar empresa durante onboarding |
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

- Java 25 LTS
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Security
- Spring Data JPA
- Bean Validation
- PostgreSQL
- Flyway
- OpenAPI / Swagger
- Micrometer / Prometheus
- JUnit / MockMvc / Mockito
- Maven
- Docker

## Testes e qualidade

A suíte possui testes de controller, service, mappers, helpers, validação de CNPJ, tratamento de exceções e filtros de segurança.

O pipeline valida:

- Java 25;
- Maven/JaCoCo;
- PostgreSQL/Flyway;
- Docker;
- regressões e integrações cross-service aplicáveis.

## Próximas evoluções

- Onboarding Orchestrator como coordenador oficial de Empresa, User, Contrato e convite no Login;
- autorização multi-tenant consistente usando `empresaId` autenticado;
- API Gateway como borda oficial;
- migração futura de HS256 compartilhado para assinatura assimétrica/JWKS.
