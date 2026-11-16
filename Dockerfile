# ==============================================================================
# Dockerfile: Password Strength Checker REST API
# Multi-stage build for minimal image footprint and enhanced security
# ==============================================================================

# ------------------------------------------------------------------------------
# Stage 1: Build the application artifact
# ------------------------------------------------------------------------------
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Cache dependencies by copying pom.xml first
COPY pom.xml .
# Copy source code
COPY src ./src

# Build production JAR (tests run during CI test phase)
RUN mvn clean package -DskipTests

# ------------------------------------------------------------------------------
# Stage 2: Production JRE runtime image
# ------------------------------------------------------------------------------
FROM eclipse-temurin:17-jre-alpine
LABEL maintainer="DevOps Assignment Team"
LABEL description="Password Strength Checker and Improvement Suggestion API"

WORKDIR /app

# Security best practice: Create non-root system user and group
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy compiled JAR from builder stage
COPY --from=builder --chown=appuser:appgroup /app/target/*.jar /app/app.jar

# Switch to unprivileged user
USER appuser

# Expose Spring Boot server port
EXPOSE 8080

# Health check using the /api/health endpoint
HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/health || exit 1

# Launch Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

