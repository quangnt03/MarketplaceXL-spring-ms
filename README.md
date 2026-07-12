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
│   ├── pom.xml
│   ├── mvnw
│   ├── mvnw.cmd
│   └── marketplace-service/
│       ├── pom.xml
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

The backend is a Maven multi-module Spring Boot workspace with one MVP service:

- Module: `backend/marketplace-service`
- Java package: `com.example.marketplace`
- Application class: `MarketplaceApplication`

Domain package boundaries are kept inside the service:

- `storefront`, `catalog`, `cart`, `checkout`, `orders`, `library`
- `merchant`, `admin`, `payment`, `review`, `files`, `webhook`
- `integration`
- `platform/auth`, `platform/aws`, `platform/cache`, `platform/messaging`, `platform/observability`

Run backend tests:

```sh
cd backend
./mvnw -pl marketplace-service test
```

Run the backend locally:

```sh
cd backend
./mvnw -pl marketplace-service spring-boot:run
```

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

Copy `.env.example` to `.env`, then start local dependencies:

```sh
docker compose up -d
```

Daily local dependencies live in `compose.yaml`:

- Oracle Free for persistence
- LocalStack for AWS-like local services

Parity-only dependencies live in `compose.parity.yaml`:

- Redis
- RabbitMQ

Helper scripts:

```sh
./scripts/dev-up.sh
./scripts/dev-status.sh
./scripts/smoke-local.sh
./scripts/dev-down.sh
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
./mvnw -pl marketplace-service test
```

```sh
cd frontend/web
pnpm install --frozen-lockfile
pnpm build
```
