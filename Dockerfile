FROM openjdk:8-jdk-alpine
MAINTAINER uo257431 uo257431@uniovi.es
VOLUME /tmp
USER progQueryWebApp

COPY target/*.jar /opt/webApp/app.jar
COPY launchDockerContainer.sh launchDockerContainer.sh
COPY installMvnPlugin.sh installMvnPlugin.sh
COPY src/main/resurces/plugin/ProgQuery.jar ProgQuery.jar
COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf 
ADD http://central.maven.org/maven2/org/hsqldb/hsqldb/2.4.0/hsqldb-2.4.0.jar /opt/hsqldb/hsqldb.jar

RUN apt-get update -y && apt-get install maven -y && apt-get install -y supervisor
RUN ./installMvnPlugin.sh
RUN rm plugin.jar && rm installMvnPlugin.sh
ENV MAVEN_HOME /etc/maven

ENTRYPOINT ["/usr/bin/supervisord"]