# CI/CD and Deployment

## Local Development

Use Docker Compose for local development dependencies.

Required local services:

- Oracle Database
- LocalStack S3
- Marketplace backend service
- Next.js frontend

Parity-only services:

- Redis
- RabbitMQ

## Environment Variables

### Backend

```text
SPRING_PROFILES_ACTIVE=dev
DATABASE_URL=jdbc:oracle:thin:@//localhost:1521/FREEPDB1
DATABASE_USERNAME=marketplace_app
DATABASE_PASSWORD=
REDIS_HOST=
REDIS_PORT=
RABBITMQ_HOST=
RABBITMQ_PORT=
RABBITMQ_DEFAULT_USER=
RABBITMQ_DEFAULT_PASS=
AWS_ENDPOINT_URL=http://localhost:4566
AWS_REGION=us-east-1
AWS_ACCESS_KEY_ID=test
AWS_SECRET_ACCESS_KEY=test
PAYMENT_PROVIDER=mock
STRIPE_SECRET_KEY=
STRIPE_WEBHOOK_SECRET=
JWT_SECRET=
```

### Frontend

```text
BACKEND_BASE_URL=http://localhost:8080
NEXTAUTH_URL=http://localhost:3000
NEXTAUTH_SECRET=
```

## GitHub Actions Pipeline

Pull request pipeline:

1. Checkout code
2. Set up Java
3. Set up Node.js
4. Install frontend dependencies
5. Build frontend
6. Validate the Gradle wrapper
7. Run `./gradlew check`
8. Build the `marketplace-service` boot JAR
9. Validate daily and parity Compose configurations
10. Build Docker images
11. Run the Docker Compose smoke test when the required runner capacity is available
12. Validate Kubernetes manifests after the future deployment path is implemented

## Deployment Stages

### Stage 1: Local Non-Container

Purpose:

- Fast development.

Components:

- Run frontend locally.
- Run backend locally.
- Run Oracle and LocalStack through Docker Compose.
- Add Redis and RabbitMQ through the parity Compose overlay when the adapter under test requires them.

### Stage 2: Docker Compose

Purpose:

- Full local system integration.

Components:

- Frontend container
- Marketplace backend container
- Oracle container
- Redis container
- RabbitMQ container
- LocalStack S3 container

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
- `/actuator/health/liveness`
- `/actuator/health/readiness`

Readiness should depend on:

- Database connection
- Redis connection when the Redis adapter is enabled
- RabbitMQ connection when the RabbitMQ adapter is enabled

## Deployment Rules

- Do not store secrets in Git.
- Use environment variables for runtime config.
- Keep local mock payment mode available.
- Use real Stripe keys only in secure environment variables.
- Use private bucket for product files.
- Public product images may use public or signed URLs depending on implementation.
