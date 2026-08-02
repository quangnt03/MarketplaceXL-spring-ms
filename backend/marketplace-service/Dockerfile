FROM eclipse-temurin:25-jdk AS build

WORKDIR /workspace

COPY gradle ./gradle
COPY gradlew settings.gradle build.gradle ./
COPY marketplace-service/build.gradle ./marketplace-service/build.gradle

RUN chmod +x gradlew \
    && ./gradlew :marketplace-service:dependencies --no-daemon

COPY marketplace-service/src ./marketplace-service/src

RUN ./gradlew :marketplace-service:bootJar --no-daemon

FROM eclipse-temurin:25-jre

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system marketplace \
    && useradd --system --gid marketplace --home-dir /app marketplace

WORKDIR /app

COPY --from=build --chown=marketplace:marketplace \
    /workspace/marketplace-service/build/libs/*.jar app.jar

USER marketplace

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
