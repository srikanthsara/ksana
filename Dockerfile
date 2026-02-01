FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy parent pom + module pom first (for dependency caching)
COPY pom.xml .
COPY backend/pom.xml backend/pom.xml

# Copy checkstyle folder (IMPORTANT)
COPY checkstyle ./checkstyle

# Download dependencies
RUN mvn -q -e -DskipTests dependency:go-offline

# Copy full backend source
COPY backend ./backend

# Build backend
RUN mvn -pl backend -am clean package -DskipTests


FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY --from=build /app/backend/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]
