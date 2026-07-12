FROM eclipse-temurin:25-jre

WORKDIR /app
COPY target/marketplace-service-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
