FROM node:22-alpine AS deps
WORKDIR /app
ENV CI=true
COPY package.json pnpm-lock.yaml pnpm-workspace.yaml ./
RUN corepack enable && pnpm install --frozen-lockfile

FROM node:22-alpine AS build
WORKDIR /app
ENV CI=true
COPY --from=deps /app/node_modules ./node_modules
COPY . .
RUN corepack enable && pnpm build

FROM node:22-alpine
WORKDIR /app
ENV CI=true
ENV NODE_ENV=production
COPY --from=build /app ./
RUN corepack enable
EXPOSE 3000
CMD ["pnpm", "start"]
