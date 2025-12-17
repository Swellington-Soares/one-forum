# Stage 1: Build
FROM gradle:8.5-jdk21-alpine AS build

WORKDIR /app

# Copy gradle files
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle

# Download dependencies
RUN gradle dependencies --no-daemon || true

# Copy source code
COPY src ./src

# Build application (skip tests for faster build)
RUN gradle bootJar --no-daemon -x test

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring

# Copy jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Change ownership
RUN chown -R spring:spring /app

USER spring

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
