## Build stage: compila il progetto e produce il JAR
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /workspace

# Pre-carica le dipendenze per migliorare la cache
COPY pom.xml ./
RUN --mount=type=cache,target=/root/.m2 mvn -B -q -DskipTests dependency:go-offline

# Copia il sorgente e compila
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B -DskipTests package

## Runtime stage: immagine finale leggera per esecuzione
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copia il JAR buildato nello stage precedente
COPY --from=build /workspace/target/*.jar /app/app.jar

# Facoltativo: file di configurazione esterno (override rispetto a quello nel JAR)
COPY src/main/resources/application.properties /app/application.properties

# Espone la porta usata da Spring Boot (default 8080)
EXPOSE 8080

# Comando di avvio
ENTRYPOINT ["java","-jar","app.jar"]

# NOTA: evita di inserire segreti direttamente nell'immagine. Imposta le variabili a runtime (es. da Kubernetes Secrets).
ENV SPRING_DATASOURCE_URL="jdbc:mysql://mysql-service:3306/fotografia?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
ENV SPRING_DATASOURCE_USERNAME="root"
ENV SPRING_DATASOURCE_PASSWORD="OCTRocks2025@"