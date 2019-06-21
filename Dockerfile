FROM openjdk:8-jdk-alpine
MAINTAINER uo257431 uo257431@uniovi.es
VOLUME /tmp

# Install the basics

RUN apk update && apk add maven supervisor
ENV MAVEN_HOME /etc/maven
EXPOSE 80/tcp

# Install progQuery webApp

COPY . build/
RUN mvn -f build/pom.xml -Dmaven.test.skip=true package
RUN mkdir -p /opt/webApp
RUN cp build/target/*.jar /opt/webApp/app.jar
RUN rm -r -f build/

# Install the plugin

COPY src/main/resources/plugin/ProgQuery.jar ProgQuery.jar
RUN mvn install:install-file -DcreateChecksum=true -Dpackaging=jar -Dfile=ProgQuery.jar -DgroupId=es.uniovi.progQuery -DartifactId=progQuery -Dversion=0.0.1-SNAPSHOT -DgeneratePom=true
RUN rm ProgQuery.jar

# Install HSQLDB

ADD http://central.maven.org/maven2/org/hsqldb/hsqldb/2.4.0/hsqldb-2.4.0.jar /opt/hsqldb/hsqldb.jar

# Install Supervisord

COPY supervisord.conf /etc/supervisor/supervisord.conf 

# Run

ENTRYPOINT ["/usr/bin/supervisord", "-c", "/etc/supervisor/supervisord.conf"]