

#Etapa 1
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build

#copiamos el pom y descargamos las dependencias
COPY pom.xml .
RUN mvn dependency:go-offline

#Copiamos el codigo fucnte y compilamos el proyecto
COPY src ./src
RUN mvn clean package -DskipTests

#Etapa2: Ejecutamos

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar


EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]




