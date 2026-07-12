# CI/CD and Deployment

## Local Development

Use Docker Compose for local development dependencies.

Required local services:

- Oracle Database
- Redis
- RabbitMQ
- MinIO
- Backend services
- Next.js frontend

## Environment Variables

### Backend

```text
APP_ENV=local
DATABASE_URL=
DATABASE_USERNAME=
DATABASE_PASSWORD=
REDIS_HOST=
REDIS_PORT=
RABBITMQ_HOST=
RABBITMQ_PORT=
RABBITMQ_USERNAME=
RABBITMQ_PASSWORD=
STORAGE_ENDPOINT=
STORAGE_ACCESS_KEY=
STORAGE_SECRET_KEY=
STORAGE_BUCKET_PUBLIC=
STORAGE_BUCKET_PRIVATE=
PAYMENT_PROVIDER=mock
STRIPE_SECRET_KEY=
STRIPE_WEBHOOK_SECRET=
JWT_SECRET=
```

### Frontend

```text
NEXT_PUBLIC_API_BASE_URL=
NEXT_PUBLIC_APP_BASE_URL=
```

## GitHub Actions Pipeline

Pull request pipeline:

1. Checkout code
2. Set up Java
3. Set up Node.js
4. Install frontend dependencies
5. Build frontend
6. Run backend unit tests
7. Run backend integration tests
8. Build backend artifact
9. Build Docker images
10. Optional Docker Compose smoke test
11. Optional Kubernetes manifest validation

## Deployment Stages

### Stage 1: Local Non-Container

Purpose:

- Fast development.

Components:

- Run frontend locally.
- Run backend locally.
- Run Oracle, Redis, RabbitMQ, MinIO through Docker Compose.

### Stage 2: Docker Compose

Purpose:

- Full local system integration.

Components:

- Frontend container
- Backend service containers
- Oracle container
- Redis container
- RabbitMQ container
- MinIO container

### Stage 3: Minikube

Purpose:

- Kubernetes deployment validation.

Resources:

- Deployment
- Service
- Ingress
- ConfigMap
- Secret

Do not start with production-grade Kubernetes complexity.

## Health Checks

Backend services must expose:

- `/actuator/health`
- `/actuator/info`

Readiness should depend on:

- Database connection
- Redis connection where required
- RabbitMQ connection where required

## Deployment Rules

- Do not store secrets in Git.
- Use environment variables for runtime config.
- Keep local mock payment mode available.
- Use real Stripe keys only in secure environment variables.
- Use private bucket for product files.
- Public product images may use public or signed URLs depending on implementation.
