FROM openjdk:8-jdk-alpine
MAINTAINER uo257431 uo257431@uniovi.es
VOLUME /tmp
COPY target/app.jar app.jar
COPY launchDockerInstance.sh .
COPY installMvnPlugin.sh .
COPY src/main/resurces/plugin/ProgQuery.jar ProgQuery.jar
USER progQuery
RUN apt-get update -y && apt-get install maven -y
RUN ./installMvnPlugin.sh
RUN rm plugin.jar && rm installMvnPlugin.sh
ENV MAVEN_HOME /etc/maven
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]