FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/*.jar app.jar
EXPOSE 8080
USER progQuery
RUN apt-get update -y && apt-get install maven -y
ENV MAVEN_HOME /etc/maven
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]