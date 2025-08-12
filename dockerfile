# syntax=docker/dockerfile:1

# --- Build stage ---
FROM maven:3.9.7-eclipse-temurin-21 AS build
WORKDIR /app

# cache deps first
COPY pom.xml .
RUN mvn -B -q -DskipTests dependency:go-offline

# now copy sources and build
COPY src ./src
RUN mvn -B -DskipTests clean package

# --- Runtime stage ---
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
