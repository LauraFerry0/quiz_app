# =========================
# Build stage
# =========================
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /src

# Cache dependencies first
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Build application
COPY . .
RUN mvn -q -DskipTests package

# =========================
# Runtime stage
# =========================
FROM eclipse-temurin:21-jre
WORKDIR /app

# Create paths used by the app (will be bind-mounted by Unraid)
RUN mkdir -p \
  /app/logs \
  /data/music \
  /data/posters \
  /data/profile-pictures

# Copy Spring Boot fat JAR
COPY --from=build /src/target/*.jar /app/app.jar

# Run as nobody:users for Unraid compatibility
RUN chown -R 99:100 /app /data
USER 99:100

# App port (matches application.properties default)
ENV SERVER_PORT=9090
EXPOSE 9090

# Sensible JVM defaults for containers
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"

# Start the app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
