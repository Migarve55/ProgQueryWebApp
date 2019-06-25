FROM openjdk:8-jdk-alpine
MAINTAINER uo257431 uo257431@uniovi.es
VOLUME /tmp

# Install the basics

RUN apk update && apk add maven supervisor
ENV M2_HOME /usr/bin/
EXPOSE 80/tcp
RUN addgroup -S app 
RUN adduser -S app -G app --disabled-password --no-create-home

# Install progQuery webApp

COPY . build/
RUN mvn -f build/pom.xml -Dmaven.test.skip=true package
RUN mkdir -p /opt/webApp
RUN mkdir /opt/webApp/uploads
RUN cp build/target/*.jar /opt/webApp/app.jar
RUN rm -r -f build/
RUN chown app:app /opt/webApp/app.jar

# Install the plugin

COPY plugin/ProgQuery.jar plugin/ProgQuery.jar
RUN mvn install:install-file -DcreateChecksum=true -Dpackaging=jar -Dfile=plugin/ProgQuery.jar -DgroupId=es.uniovi.progQuery -DartifactId=progQuery -Dversion=0.0.1-SNAPSHOT -DgeneratePom=true

# Install HSQLDB

ADD http://central.maven.org/maven2/org/hsqldb/hsqldb/2.4.0/hsqldb-2.4.0.jar /opt/hsqldb/hsqldb.jar
RUN chown app:app /opt/hsqldb/hsqldb.jar

# Install Supervisord

COPY supervisord.conf /etc/supervisor/supervisord.conf 

# Run

USER app:app
ENTRYPOINT ["/usr/bin/supervisord", "-c", "/etc/supervisor/supervisord.conf"]