FROM openjdk:8-jdk-alpine
MAINTAINER uo257431 uo257431@uniovi.es
VOLUME /tmp

# Install the basics

RUN apk update && apk add maven netcat-openbsd
ENV M2_HOME /usr/bin/
EXPOSE 8080/tcp
RUN addgroup -S app 
RUN adduser -S app -G app --disabled-password --no-create-home
COPY deploy/wait-for wait-for

# Install progQuery webApp

COPY . build/
COPY deploy/application-prod.properties build/src/main/resources/application.properties
RUN mvn -f build/pom.xml -Dmaven.test.skip=true package
RUN mkdir -p /opt/webApp
RUN mkdir /opt/webApp/uploads
RUN cp build/target/*.jar /opt/webApp/app.jar
RUN rm -r -f build/
RUN chown -R app:app /opt/webApp/

# Install the plugin

COPY plugin/ProgQuery.jar plugin/ProgQuery.jar
RUN mvn install:install-file -DcreateChecksum=true -Dpackaging=jar -Dfile=plugin/ProgQuery.jar -DgroupId=es.uniovi.progQuery -DartifactId=progQuery -Dversion=0.0.1-SNAPSHOT -DgeneratePom=true

# Run

USER app:app
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/opt/webApp/app.jar"]
