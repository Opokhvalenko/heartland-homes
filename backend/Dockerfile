# Stage 1: Build with Maven
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source files
COPY src ./src
COPY checkstyle.xml .
COPY checkstyle-suppressions.xml .

# Build the application
RUN mvn clean package -DskipTests

# Extract layers from fat JAR
RUN java -Djarmode=layertools -jar target/*.jar extract

# Stage 2: Final minimal runtime image
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
EXPOSE 8080
