# ---- Stage 1: Build the application ----
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Set work directory
WORKDIR /app

# Copy only the files needed to build
COPY pom.xml .
COPY src ./src

# Build Spring Boot app (skip tests for faster deploy)
RUN mvn clean package -DskipTests

# ---- Stage 2: Run the application ----
FROM eclipse-temurin:17-jdk-jammy AS runner

# Set work directory
WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Set IST as default timezone
ENV TZ=Asia/Kolkata
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Set Java memory options (adjust for Render free tier)
ENV JAVA_OPTS="-Xms128m -Xmx384m"

# Expose port (Render sets dynamically, default 8080 locally)
EXPOSE 8080

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --server.port=${PORT:-8080}"]
