# Build stage
FROM maven:3.8-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Environment variables with defaults
ENV DB_HOST=localhost
ENV DB_PORT=5432
ENV DB_NAME=ai_marketplace
ENV DB_USERNAME=postgres
ENV DB_PASSWORD=postgres
ENV MINIO_ENDPOINT=http://localhost:9000
ENV MINIO_ACCESS_KEY=minioadmin
ENV MINIO_SECRET_KEY=minioadmin
ENV MINIO_BUCKET=ai-marketplace

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
