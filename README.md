# 🔐 API de Login — FATEC

API REST desenvolvida em **Java + Spring Boot** para gerenciamento de usuários e autenticação, utilizando **Spring Security, JWT e MongoDB**.

O projeto foi desenvolvido com foco em segurança, autenticação stateless e organização modular, disponibilizando endpoints para cadastro, login, logout, recuperação de senha e consulta do usuário autenticado.

---

## 📌 Sobre o projeto

O projeto consiste em uma API responsável pelo sistema de autenticação de uma aplicação.

A API permite:

* 👤 Cadastro de usuários
* 📝 Criação de usuário com login automático
* 🔑 Login utilizando usuário e senha
* 🍪 Armazenamento do JWT em cookie `HttpOnly`
* 🚪 Logout
* 🔎 Consulta dos dados do usuário autenticado
* 🔐 Controle de acesso utilizando roles
* 👑 Rotas exclusivas para administradores
* 🗄️ Persistência dos usuários no MongoDB
* 🔒 Criptografia das senhas utilizando BCrypt
* 📚 Documentação da API com OpenAPI/Swagger

---

## 🛠️ Tecnologias utilizadas

| Tecnologia           | Utilização                    |
| -------------------- | ----------------------------- |
| ☕ Java 17            | Linguagem principal           |
| 🌱 Spring Boot 4.0.5 | Framework da aplicação        |
| 🔐 Spring Security   | Segurança e autenticação      |
| 🎟️ JWT              | Geração e validação de tokens |
| 🍃 MongoDB           | Banco de dados                |
| 📦 Maven             | Gerenciamento de dependências |
| 📖 SpringDoc OpenAPI | Documentação da API           |
| 🧪 Spring Boot Test  | Testes                        |
| 🐳 Docker            | Containerização               |

---

## 🏗️ Estrutura do projeto

O projeto utiliza uma estrutura modular dividida em dois módulos principais:

```text
login-api-main/
│
├── domain/
│   ├── src/
│   │   └── main/
│   │       └── java/
│   │           └── br/com/fatec/fretec/
│   │               ├── entity/
│   │               └── repository/
│   │
│   └── pom.xml
│
├── springframework/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── br/com/fatec/fretec/
│   │       │       ├── configuration/
│   │       │       ├── controller/
│   │       │       ├── repository/
│   │       │       └── security/
│   │       │
│   │       └── resources/
│   │           ├── application.yml
│   │           └── static/
│   │               └── index.html
│   │
│   └── pom.xml
│
├── Dockerfile
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

### 📦 Módulo `domain`

Responsável pelas entidades e regras relacionadas ao domínio da aplicação.

Entre os principais componentes estão:

* `User`
* `Login`
* `Token`
* `UserRole`
* `UserRepository`

### 🌱 Módulo `springframework`

Responsável pela implementação da aplicação utilizando Spring Boot.

Contém:

* Controllers
* Configurações de segurança
* JWT
* Repositórios
* DTOs de entrada e saída
* Integração com MongoDB
* Aplicação principal

---

# 🔐 Autenticação

A autenticação utiliza **JWT (JSON Web Token)**.

O fluxo funciona da seguinte maneira:

```text
┌──────────────┐
│    Cliente   │
└──────┬───────┘
       │
       │ Usuário + Senha
       ▼
┌─────────────────────┐
│   AuthController    │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ Spring Security     │
│ + BCrypt            │
└──────────┬──────────┘
           │
           │ Credenciais válidas
           ▼
┌─────────────────────┐
│     JWT gerado      │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ Cookie HttpOnly     │
│   access_token      │
└─────────────────────┘
```

O token é armazenado no cookie:

```text
access_token
```

Esse cookie possui configurações de segurança como:

* `HttpOnly`
* `SameSite=Strict`
* `Path=/`
* Tempo de expiração definido pelo JWT

Em ambiente de produção com HTTPS, a propriedade `secure` deve ser habilitada.

---

# 🔒 Segurança

A aplicação utiliza `Spring Security` para controlar o acesso aos endpoints.

As senhas não são armazenadas diretamente. O projeto utiliza:

```java
BCryptPasswordEncoder
```

para realizar o hash das senhas.

Além disso, a aplicação utiliza uma arquitetura **stateless**, ou seja, não mantém sessões tradicionais no servidor.

```java
SessionCreationPolicy.STATELESS
```

As requisições autenticadas são validadas através do JWT.

---

# 👥 Controle de acesso

O sistema possui diferentes níveis de acesso através de roles.

Exemplo:

```text
USER
ADMIN
```

Rotas administrativas exigem a role:

```text
ADMIN
```

Exemplo de configuração:

```java
.requestMatchers("/fatec/login/v1/admin/**")
.hasRole("ADMIN")
```

Enquanto algumas rotas, como cadastro e autenticação, são públicas.

---

# 🌐 Endpoints

A API utiliza o prefixo:

```text
/fatec/login
```

## 🔑 Login

### POST

```text
/fatec/login/v1/auth
```

Realiza a autenticação do usuário.

### Exemplo de requisição

```json
{
  "username": "usuario",
  "password": "123456"
}
```

### Resposta

```json
{
  "id": "id-do-usuario",
  "username": "usuario",
  "roles": [
    "USER"
  ]
}
```

O JWT é enviado através do cookie `access_token`.

---

## 👤 Cadastro de usuário

### POST

```text
/fatec/login/v1/user/save
```

Cria um novo usuário no banco de dados.

Exemplo:

```json
{
  "username": "usuario",
  "password": "123456",
  "email": "usuario@email.com",
  "cep": "00000-000",
  "roles": [
    "USER"
  ]
}
```

---

## 👤 Cadastro + Login automático

### POST

```text
/fatec/login/v1/user/create
```

Esse endpoint realiza duas operações:

1. Cria o usuário.
2. Realiza o login automaticamente.

Depois da criação, o JWT é gerado e armazenado no cookie `access_token`.

---

## 🚪 Logout

### POST

```text
/fatec/login/v1/logout
```

Remove o cookie de autenticação.

O cookie `access_token` é expirado através de:

```text
maxAge=0
```

---

## 👤 Usuário autenticado

### GET

```text
/fatec/login/v1/me
```

Retorna as informações do usuário atualmente autenticado.

Esse endpoint utiliza o JWT presente no cookie para identificar o usuário.

Exemplo de resposta:

```json
{
  "id": "123",
  "username": "usuario",
  "roles": [
    "USER"
  ]
}
```

---

## 🔑 Recuperação de senha

### GET

```text
/fatec/login/v1/auth/forget/{username}
```

Endpoint destinado ao fluxo de recuperação de senha.

Exemplo:

```text
/fatec/login/v1/auth/forget/leticia
```

Resposta atual:

```text
Olá leticia enviamos sua senha para o seu email
```

> ⚠️ Atualmente esse endpoint funciona como uma implementação demonstrativa. Para produção, seria necessário integrar um serviço de envio de e-mail e implementar um fluxo seguro de recuperação de senha.

---

# 🗄️ Banco de dados

O projeto utiliza **MongoDB**.

A configuração está localizada em:

```text
springframework/src/main/resources/application.yml
```

Configuração padrão:

```yaml
spring:
  mongodb:
    uri: mongodb://localhost:27017/fatec
```

O banco utilizado é:

```text
fatec
```

E a aplicação está configurada para criar índices automaticamente:

```yaml
spring:
  data:
    mongodb:
      auto-index-creation: true
```

---

# ⚙️ Configuração da aplicação

O servidor utiliza a porta:

```text
8081
```

Portanto, após iniciar a aplicação, a API estará disponível em:

```text
http://localhost:8081
```

---

# 🚀 Como executar o projeto

## 1. Pré-requisitos

Antes de executar o projeto, instale:

* Java 17
* Maven
* MongoDB
* Git

Verifique o Java:

```bash
java -version
```

O projeto foi configurado para utilizar:

```text
Java 17
```

---

## 2. Inicie o MongoDB

Certifique-se de que o MongoDB esteja executando localmente.

A aplicação espera encontrar o banco em:

```text
mongodb://localhost:27017
```

---

## 3. Clone o projeto

```bash
git clone <URL_DO_REPOSITORIO>
```

Entre na pasta:

```bash
cd login-api-main
```

---

## 4. Compile o projeto

Utilizando Maven:

```bash
mvn clean install
```

Ou utilizando o Maven Wrapper:

### Windows

```bash
mvnw.cmd clean install
```

### Linux/macOS

```bash
./mvnw clean install
```

---

## 5. Execute a aplicação

Entre no módulo Spring:

```bash
cd springframework
```

Execute:

```bash
mvn spring-boot:run
```

Ou:

```bash
../mvnw spring-boot:run
```

A aplicação será iniciada na porta:

```text
8081
```

---

# 🖥️ Interface de testes

O projeto também possui uma interface HTML simples para testar o sistema de autenticação.

Ela está localizada em:

```text
springframework/src/main/resources/static/index.html
```

Depois que a aplicação estiver executando, acesse:

```text
http://localhost:8081
```

A interface permite testar funcionalidades como:

* Cadastro
* Login
* Logout
* Sessão autenticada
* Consulta do usuário
* Acesso administrativo

---

# 📖 Swagger / OpenAPI

O projeto utiliza:

```text
SpringDoc OpenAPI
```

para documentação dos endpoints.

Após iniciar a aplicação, a documentação pode ser acessada pelo Swagger UI em:

```text
http://localhost:8081/swagger-ui.html
```

ou:

```text
http://localhost:8081/swagger-ui/index.html
```

A documentação permite visualizar e testar os endpoints da API.

---

# 🐳 Docker

O projeto também possui um:

```text
Dockerfile
```

permitindo a criação de uma imagem Docker da aplicação.

Exemplo:

```bash
docker build -t login-api .
```

Depois:

```bash
docker run -p 8081:8081 login-api
```

> Para funcionamento completo, o container da aplicação também precisa conseguir acessar uma instância do MongoDB.

---

# 🔄 Fluxo da aplicação

O fluxo principal pode ser representado da seguinte forma:

```text
              ┌───────────────┐
              │    Cliente    │
              └───────┬───────┘
                      │
              Login / Cadastro
                      │
                      ▼
            ┌──────────────────┐
            │ Spring Boot API  │
            └────────┬─────────┘
                     │
          ┌──────────┴──────────┐
          │                     │
          ▼                     ▼
 ┌────────────────┐    ┌─────────────────┐
 │ Spring Security│    │    MongoDB      │
 └───────┬────────┘    └─────────────────┘
         │
         ▼
 ┌─────────────────┐
 │ Validação senha │
 │     BCrypt      │
 └────────┬────────┘
          │
          ▼
 ┌─────────────────┐
 │   Geração JWT   │
 └────────┬────────┘
          │
          ▼
 ┌─────────────────┐
 │ HttpOnly Cookie │
 │  access_token   │
 └─────────────────┘
```

---

# 📂 Principais componentes

### `LoginApplication`

Classe principal responsável por iniciar a aplicação Spring Boot.

```text
LoginApplication.java
```

### `AuthController`

Responsável pelas operações de:

* Login
* Logout
* Recuperação de senha
* Consulta do usuário autenticado

```text
AuthController.java
```

### `UserController`

Responsável pelo cadastro e criação de usuários.

```text
UserController.java
```

### `SecurityConfig`

Centraliza as configurações de segurança da aplicação.

```text
SecurityConfig.java
```

Define:

* BCrypt
* Authentication Manager
* JWT Filter
* Rotas públicas
* Rotas protegidas
* Roles
* Sessão Stateless

### `JwtSecurity`

Responsável pela criação e validação dos tokens JWT.

### `JwtAuthFilterSecurity`

Filtro responsável por verificar o JWT presente nas requisições.

### `TokenSecurity`

Responsável pelo processo de autenticação e geração do token.

### `UserDetailsSecurity`

Integra os usuários da aplicação com o mecanismo de autenticação do Spring Security.

---

# 🔐 Boas práticas de segurança utilizadas

O projeto implementa algumas práticas importantes:

* Senhas protegidas com BCrypt
* Autenticação baseada em JWT
* Cookie `HttpOnly`
* `SameSite=Strict`
* Sessões Stateless
* Controle de acesso por roles
* Filtro de autenticação JWT
* Separação entre domínio e framework

Para produção, recomenda-se também:

* Utilizar HTTPS
* Habilitar `cookie.secure=true`
* Utilizar variáveis de ambiente para informações sensíveis
* Utilizar uma chave JWT segura armazenada fora do código
* Implementar recuperação de senha real por e-mail
* Adicionar validação dos dados recebidos
* Implementar testes automatizados adicionais
* Configurar CORS de acordo com o frontend utilizado

---

# 🎯 Objetivo acadêmico

O projeto foi desenvolvido como uma aplicação acadêmica para demonstrar a implementação de uma API REST com autenticação e autorização.

Os principais conceitos trabalhados são:

* Desenvolvimento de APIs REST
* Arquitetura modular
* Spring Boot
* Spring Security
* Autenticação JWT
* Criptografia de senhas
* Banco de dados NoSQL
* MongoDB
* Controle de acesso
* Cookies HTTP
* Maven
* Docker
* OpenAPI

---

# 👩‍💻 Desenvolvimento

Projeto desenvolvido no contexto acadêmico da:

**FATEC — Faculdade de Tecnologia**

Área:

**Análise e Desenvolvimento de Sistemas**

---

## 📄 Licença

Este projeto possui finalidade acadêmica e educacional.
