# Desafio Itaú — API de Transações e Estatísticas

API REST em Java (Spring Boot) que **recebe transações** e **retorna estatísticas** sobre elas. Os dados são armazenados **apenas em memória** (sem banco de dados e sem cache externo). Todas as requisições e respostas usam **JSON**.

---

## Índice

- [Requisitos e stack](#requisitos-e-stack)
- [Construir e executar](#construir-e-executar)
- [Funcionalidades principais](#funcionalidades-principais)
- [Endpoints](#endpoints)
- [Contratos JSON](#contratos-json)
- [Códigos de resposta e erros](#códigos-de-resposta-e-erros)
- [Configuração](#configuração)
- [Documentação interativa (Swagger)](#documentação-interativa-swagger)

---

## Requisitos e stack

- **Java 25** (definido no `build.gradle`)
- **Gradle** (wrapper incluído: `gradlew` / `gradlew.bat`)
- **Spring Boot 4.x** com Spring Web MVC, Validation, Actuator
- **SpringDoc OpenAPI** para Swagger UI

Não é necessário banco de dados, Redis ou qualquer serviço externo.

---

## Construir e executar

### Pré-requisitos

- JDK 25 instalado (ou compatível com a versão configurada no projeto)
- Terminal na raiz do repositório

### Build

Na pasta **`backend`**:

```bash
cd backend
./gradlew build
```

No Windows:

```bash
cd backend
gradlew.bat build
```

Isso compila o código, executa os testes e gera o JAR.

### Executar a aplicação

Ainda dentro de `backend`:

```bash
./gradlew bootRun
```

Ou executando o JAR gerado:

```bash
./gradlew bootJar
java -jar build/libs/backend-0.0.1-SNAPSHOT.jar
```

A API sobe por padrão em **http://localhost:8080**.

### Testes

```bash
./gradlew test
```

---

## Funcionalidades principais

1. **Registro de transações**  
   POST com `valor` e `dataHora` (ISO-8601). Apenas transações dentro da **janela configurável** (por padrão 60 segundos a partir de “agora”) entram no cálculo das estatísticas.

2. **Consulta de estatísticas**  
   GET retorna: quantidade (`count`), soma (`sum`), média (`avg`), mínimo (`min`) e máximo (`max`) das transações na janela.

3. **Limpeza de transações**  
   DELETE remove todas as transações armazenadas em memória.

4. **Configuração da janela**  
   A janela (em segundos) pode ser lida (GET) e alterada (PATCH) em tempo de execução (1 a 86400 segundos). O valor inicial vem de `application.properties` (padrão: 60).

5. **Armazenamento em memória**  
   Store em memória com estrutura thread-safe (`CopyOnWriteArrayList`). Sem banco de dados e sem cache externo.

6. **Documentação OpenAPI**  
   Swagger UI em `/swagger-ui.html` e API docs em `/v3/api-docs`.

7. **Health check**  
   Actuator expõe o endpoint de saúde conforme configurado em `application.properties`.

---

## Endpoints

Base URL: **http://localhost:8080** (ou a porta configurada).

| Método | Path | Descrição |
|--------|------|-----------|
| POST   | `/api/v1/transacoes` | Registrar uma transação |
| DELETE | `/api/v1/transacoes` | Limpar todas as transações |
| GET    | `/api/v1/estatisticas` | Obter estatísticas da janela |
| GET    | `/api/v1/config/estatisticas/janela` | Obter janela atual (segundos) |
| PATCH  | `/api/v1/config/estatisticas/janela` | Alterar janela (segundos) |

---

### POST `/api/v1/transacoes`

Registra uma transação. Corpo obrigatório em JSON.

**Request (exemplo):**

```json
{
  "valor": "10.50",
  "dataHora": "2025-02-01T12:00:00Z"
}
```

- **valor**: número ≥ 0 (obrigatório).
- **dataHora**: data/hora em ISO-8601, não pode ser futura (obrigatório).

**Respostas:**

- **201** — Transação registrada.
- **400** — Corpo inválido (ex.: JSON malformado).
- **422** — Validação falhou (valor negativo, dataHora futura, campos faltando, etc.).

---

### DELETE `/api/v1/transacoes`

Remove todas as transações em memória.

**Respostas:**

- **200** — Transações removidas.

---

### GET `/api/v1/estatisticas`

Retorna estatísticas das transações cuja `dataHora` está dentro da janela configurada (por padrão, últimos 60 segundos em relação ao momento da consulta).

**Resposta 200 (exemplo):**

```json
{
  "count": 3,
  "sum": 45.50,
  "avg": 15.166667,
  "min": 5.00,
  "max": 25.50
}
```

Quando não há transações na janela:

```json
{
  "count": 0,
  "sum": 0,
  "avg": 0,
  "min": 0,
  "max": 0
}
```

---

### GET `/api/v1/config/estatisticas/janela`

Retorna a janela em segundos usada no cálculo das estatísticas.

**Resposta 200 (exemplo):**

```json
{
  "janelaSegundos": 60
}
```

---

### PATCH `/api/v1/config/estatisticas/janela`

Altera a janela em segundos. Efeito imediato nas próximas consultas de estatísticas.

**Request (exemplo):**

```json
{
  "janelaSegundos": 120
}
```

- **janelaSegundos**: inteiro entre 1 e 86400 (obrigatório).

**Respostas:**

- **200** — Janela alterada; corpo retorna o valor atual (mesmo formato do GET da janela).
- **422** — Validação falhou (fora do intervalo ou tipo inválido).

---

## Contratos JSON

Os nomes dos campos devem ser **exatos** (camelCase):

- Transação: `valor`, `dataHora`
- Estatísticas: `count`, `sum`, `avg`, `min`, `max`
- Config janela: `janelaSegundos`

Content-Type das requisições com corpo: `application/json`. Respostas com corpo também em `application/json`.

---

## Códigos de resposta e erros

| Código | Situação |
|--------|----------|
| 200 | OK (GET/PATCH/DELETE com sucesso) |
| 201 | Transação criada (POST transações) |
| 400 | Corpo da requisição inválido (ex.: JSON não legível) |
| 422 | Validação falhou (Bean Validation: campos obrigatórios, valor &lt; 0, dataHora futura, janela fora de 1–86400) |

Em 400 e 422 o corpo da resposta é vazio (sem detalhes no JSON).

---

## Configuração

Arquivo principal: **`backend/src/main/resources/application.properties`**.

| Propriedade | Descrição | Exemplo |
|-------------|-----------|---------|
| `app.estatisticas.janela-segundos` | Janela inicial para estatísticas (segundos) | `60` |
| `management.endpoints.web.exposure.include` | Endpoints do Actuator expostos | `health` |
| `springdoc.api-docs.path` | Caminho da API OpenAPI | `/v3/api-docs` |
| `springdoc.swagger-ui.path` | Caminho da Swagger UI | `/swagger-ui.html` |

A janela pode ser alterada em runtime via **PATCH** em `/api/v1/config/estatisticas/janela` (1 a 86400 segundos).

---

## Documentação interativa (Swagger)

Com a aplicação rodando:

- **Swagger UI**: http://localhost:8080/swagger-ui.html  
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs  

Lá é possível ver todos os endpoints, contratos e testar as chamadas.

---

## Resumo para quem pega o projeto pela primeira vez

1. Entrar na pasta **`backend`**.
2. Rodar **`./gradlew build`** para compilar e testar.
3. Rodar **`./gradlew bootRun`** para subir a API em http://localhost:8080.
4. Abrir **http://localhost:8080/swagger-ui.html** para explorar e testar os endpoints.
5. Usar **POST** em `/api/v1/transacoes` para registrar transações e **GET** em `/api/v1/estatisticas` para ver as estatísticas na janela configurada.
