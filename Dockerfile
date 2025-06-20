# --- Stage 1: Build the application ---
FROM maven:3.9.4-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy only pom.xml first to leverage Docker layer cache
COPY pom.xml .

# Purge any old cached jjwt version
RUN rm -rf /root/.m2/repository/io/jsonwebtoken

# Download dependencies
RUN mvn dependency:go-offline

# Copy source code
COPY . .

# Clean build and skip tests
RUN mvn clean package -DskipTests

# --- Stage 2: Create runtime image ---
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy the jar file from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the default port
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
