FROM openjdk:11
RUN mkdir /opt/ncomplo
COPY target/putaspelotas-wc22-1.0.0.jar /opt/ncomplo
ENTRYPOINT ["java","-Dspring.profiles.active=pro", "-jar", "/opt/ncomplo/putaspelotas-wc22-1.0.0.jar"]