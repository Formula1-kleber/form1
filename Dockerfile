# Uma única Dockerfile para os 5 serviços. Escolha o serviço com --build-arg SERVICE=auth|auction|notification|report|gateway
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
ARG SERVICE
RUN mvn -q -B -pl ${SERVICE}/springframework -am package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
ARG SERVICE
COPY --from=build /app/${SERVICE}/springframework/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
