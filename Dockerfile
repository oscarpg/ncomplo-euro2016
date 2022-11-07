FROM openjdk:11
COPY target/*.jar app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=pro", "-jar", "/opt/ncomplo/putaspelotas-wc22-1.0.0.jar"]