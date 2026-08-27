# ---------- STAGE 1: BUILD ----------
FROM maven:3.9.11-eclipse-temurin-25 AS build

WORKDIR /app
COPY pom.xml .
RUN mvn -B -q -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------- STAGE 2: RUNTIME ----------
FROM eclipse-temurin:25-jre

WORKDIR /app
RUN groupadd --system app && useradd --system --gid app app
COPY --from=build --chown=app:app /app/target/*.jar /app/app.jar

USER app
EXPOSE 8081

ENTRYPOINT ["java","-jar","/app/app.jar"]
