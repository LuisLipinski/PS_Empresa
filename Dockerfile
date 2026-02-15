# ---------- STAGE 1 : BUILD ----------
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copia apenas dependências primeiro (cache inteligente)
COPY pom.xml .
RUN mvn -B -q -e -DskipTests dependency:go-offline

# Copia o resto do código
COPY src ./src

# Gera o jar
RUN mvn clean package -DskipTests


# ---------- STAGE 2 : RUNTIME ----------
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copia o jar gerado no estágio anterior
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
