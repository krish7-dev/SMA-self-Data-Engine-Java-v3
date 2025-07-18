# ---- Stage 1: Build the application ----
FROM eclipse-temurin:17-jdk AS builder

# Set work directory
WORKDIR /app

# Copy Gradle/Maven build files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Build Spring Boot app (skip tests for faster deploy)
RUN ./mvnw clean package -DskipTests

# ---- Stage 2: Run the application ----
FROM eclipse-temurin:17-jdk-jammy AS runner

# Set work directory
WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Set IST as default timezone
ENV TZ=Asia/Kolkata
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Set Java memory options (adjust for 512MB Render free tier)
ENV JAVA_OPTS="-Xms128m -Xmx384m"

# Expose port (Render sets dynamically, but 8080 as default)
EXPOSE 8080

# Start the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --server.port=${PORT:-8080}"]
