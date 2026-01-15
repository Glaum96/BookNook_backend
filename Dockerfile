FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Install Maven
RUN apk add --no-cache maven

# Copy pom.xml first for dependency caching
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B || true

# Copy all source code and resources
COPY mvnw .
COPY .mvn .mvn
COPY src src

# Create graphql-client directory if it doesn't exist (required by Maven plugin)
RUN mkdir -p src/main/resources/graphql-client

# Build the application using Maven directly (not wrapper)
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the jar from build stage
COPY --from=build /app/target/takterrassen_backend-0.0.1-SNAPSHOT.jar app.jar

# Expose the port
EXPOSE 9090

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
