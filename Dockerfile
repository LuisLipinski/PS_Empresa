# Imagem base com Java 17 leve
FROM eclipse-temurin:17-jdk-jammy

# Diretório interno do container
WORKDIR /app

# Copia o jar gerado pelo Maven
COPY target/ps_empresa-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta do MS
EXPOSE 8081

# Comando de inicialização
ENTRYPOINT ["java","-jar","/app/app.jar"]
