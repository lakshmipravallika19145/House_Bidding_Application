# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw

COPY src ./src
RUN ./mvnw clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081
# Render sets PORT at runtime; bind to all interfaces
CMD ["sh", "-c", "java -Dserver.port=${PORT:-8081} -Dserver.address=0.0.0.0 -jar app.jar"]
