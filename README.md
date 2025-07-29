# 🐾 My Pet Admin – MS: Empresa

Este microsserviço faz parte da aplicação **My Pet Admin**, sendo responsável pela gestão de **empresas (Petshops)**. Inclui operações de cadastro, listagem, edição e exclusão lógica de empresas, além da integração com o microsserviço de contratos.

---

## 📦 Funcionalidades

- ✅ Cadastro de nova empresa
- 🔍 Listagem de empresas por:
    - ID
    - CNPJ
- ✏️ Atualização de dados da empresa
- 🗑️ Exclusão lógica (alteração de status para `DESATIVADO`)
- 🔐 Criação automática do **usuário master** ao cadastrar uma nova empresa (via MS externo)
- 🤝 Integração com MS de contrato (criação automática de contrato ao cadastrar empresa)

---

## 🧱 Estrutura do Projeto

ps_empresa/
├── controller/
│ └── EmpresaController.java
├── service/
│ └── EmpresaServiceImpl.java
├── model/
│ └── Empresa.java
├── dto/
│ ├── EmpresaRequestDTO.java
│ └── EmpresaResponseDTO.java
├── mapper/
│ └── EmpresaMapper.java
├── exception/
│ ├── EmpresaExistenteException.java
│ ├── EmpresaNaoEncontradaException.java
│ ├── EmailExistenteException.java
│ └── GlobalExceptionHandler.java
├── util/
│ └── CnpjValidator.java
└── config/
└── SwaggerConfig.java


---

## ⚙️ Tecnologias Utilizadas

- Java 21+
- Spring Boot 3.x
- Spring Web
- Spring Data JPA
- Hibernate
- PostgreSQL (produção)
- H2 (testes)
- Lombok
- OpenAPI / Swagger
- JUnit 5 + MockMvc
- Maven

---

## 🧪 Testes

- `@SpringBootTest` para testes de contexto
- `@WebMvcTest` com `MockMvc` no `EmpresaController`
- Testes unitários para `CnpjValidator`
- Cobertura monitorada via JaCoCo

---

## 🔐 Regras de Negócio

- CNPJ deve ter exatamente 14 dígitos e ser válido conforme cálculo de dígitos verificadores.
- E-mail e CNPJ devem ser únicos por empresa.
- O campo `endereco` é montado dinamicamente a partir de `rua`, `número`, `complemento` e `bairro`.
- Status padrão ao cadastrar: `ATIVO`
- Exclusão é lógica, alterando o status para `DESATIVADO`.

---

## 🚀 Executando Localmente

### Pré-requisitos

- Java 21+
- Maven 3.8+
- PostgreSQL rodando com banco `mypetadmin`

### Executar com perfil local (PostgreSQL)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev


### Executar testes com H2 (perfil test)

mvn test


### 📁 Endpoints Disponíveis

Método	URL	Descrição
POST	/empresas/createEmpresas	Cadastra uma nova empresa
GET	/empresas/{id}	Busca empresa por ID
GET	/empresas/cnpj/{cnpj}	Busca empresa por CNPJ
PUT	/empresas/{id}	Atualiza empresa
DELETE	/empresas/{id}	Desativa (exclusão lógica)

## A documentação completa pode ser acessada via:

http://localhost:8080/swagger-ui.html


### 🧩 Integrações

- 📄 Ao cadastrar uma empresa, um contrato é gerado automaticamente (via MS ps_contrato)

- 👤 Usuário master criado automaticamente (via MS ps_user)

### Convenções

- DTOs com validação via jakarta.validation

- Mapper responsável por converter DTO ↔️ Entity

- Exceptions personalizadas com tratamento global via @ControllerAdvice

### Desenvolvedores

- 👨‍💻 Backend: Luis Lipinski

- 📅 Projeto educacional com fins de aprendizado e futura comercialização.

### Licença

Este projeto é de uso educacional. O código está aberto para estudo, mas a comercialização depende de autorização dos autores.