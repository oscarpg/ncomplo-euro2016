FROM openjdk:11
RUN mkdir /opt/ncomplo
COPY target/ncomplo.jar /opt/ncomplo
ENTRYPOINT ["java","-Dspring.profiles.active=pro", "-jar", "/opt/ncomplo/ncomplo.jar"]