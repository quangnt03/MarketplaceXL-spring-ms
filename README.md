# Marketplace Platform

Local-first marketplace platform workspace for the MVP source of truth.

## Structure

```text
marketplace-platform/
├── README.md
├── AGENTS.md
├── .env.example
├── compose.yaml
├── compose.parity.yaml
├── backend/
│   ├── build.gradle
│   ├── settings.gradle
│   ├── gradlew
│   ├── gradlew.bat
│   └── marketplace-service/
│       ├── build.gradle
│       ├── Dockerfile
│       └── src/
├── frontend/
│   └── web/
│       ├── package.json
│       ├── pnpm-lock.yaml
│       ├── next.config.ts
│       ├── Dockerfile
│       ├── src/
│       └── e2e/
├── infra/
│   ├── localstack/
│   ├── oracle/
│   ├── parity/
│   ├── aws/
│   └── k8s/
├── contracts/
│   ├── openapi/marketplace-api.yaml
│   ├── asyncapi/marketplace-events.yaml
│   └── webhooks/signature-contracts.md
├── scripts/
└── .github/workflows/
```

## Backend

The backend is a Gradle multi-project Spring Boot workspace with one deployable MVP service:

- Module: `backend/marketplace-service`
- Java package: `com.example.marketplace`
- Application class: `MarketplaceApplication`

Spring Modulith enforces the domain package boundaries inside the service:

- `storefront`, `catalog`, `cart`, `checkout`, `orders`, `library`
- `merchant`, `admin`, `payment`, `review`, `files`, `webhook`
- `integration`
- `platform/auth`, `platform/aws`, `platform/cache`, `platform/messaging`, `platform/observability`

Run backend tests:

```sh
cd backend
./gradlew check
```

Run the backend locally:

```sh
cd backend
./gradlew :marketplace-service:bootRun
```

Gradle is the only JVM build tool.
New marketplace modules are added as top-level packages under `com.example.marketplace`, declare their dependencies in `package-info.java`, and expose cross-module contracts only through named interfaces such as `platform::api`.

## Frontend

The frontend is a Next.js TypeScript app in `frontend/web`.

Feature folders mirror the marketplace user-facing areas:

- `storefront`, `product`, `search`
- `cart`, `checkout`, `library`
- `merchant`, `admin`

Run the frontend locally:

```sh
cd frontend/web
pnpm install
pnpm dev
```

## Local Infrastructure

Copy `.env.example` to `.env.local`, then start the complete local stack:

```sh
docker compose --env-file .env.local up --build
```

The frontend is available at `http://localhost:3000` and the backend at `http://localhost:8080`.

Daily local services live in `compose.yaml`:

- Oracle Free for persistence
- LocalStack for AWS-like local services
- Spring Boot marketplace backend
- Next.js marketplace frontend

Parity-only dependencies live in `compose.parity.yaml`:

- Redis
- RabbitMQ

Helper scripts:

```sh
sh ./scripts/dev-up.sh
sh ./scripts/dev-status.sh
sh ./scripts/smoke-local.sh
sh ./scripts/dev-down.sh
```

## Contracts

API and event contracts live under `contracts`:

- OpenAPI: `contracts/openapi/marketplace-api.yaml`
- AsyncAPI: `contracts/asyncapi/marketplace-events.yaml`
- Webhook signatures: `contracts/webhooks/signature-contracts.md`

## Guardrails

This is a marketplace MVP. Keep project naming as `marketplace`, keep implementation in `marketplace-service` until a split is explicitly requested, and follow `AGENTS.md` before adding features.

## Validation

Current structure checks:

```sh
cd backend
./gradlew check :marketplace-service:bootJar
```

```sh
cd frontend/web
pnpm install --frozen-lockfile
pnpm build
```
