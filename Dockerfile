# syntax = docker/dockerfile:1.2
#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/putaspelotas/src
COPY pom.xml /home/putaspelotas
RUN mvn -DskipTests -f /home/putaspelotas/pom.xml clean package

#
# Package stage
#
FROM openjdk:8-jre-slim
RUN --mount=type=secret,id=application-pro.properties,dst=/src/main/resources/application-pro.properties
COPY --from=build /home/putaspelotas/target/putaspelotas-wc22-1.0.0.jar /usr/local/lib/putaspelotas-wc22.jar
EXPOSE 8080
ENTRYPOINT ["java","-Dspring.profiles.active=pro", "-jar","/usr/local/lib/putaspelotas-wc22.jar"]