FROM openjdk:11
LABEL version="1.0"
LABEL maintainer="uo257431@uniovi.es"
VOLUME /tmp

# Install the basics

RUN apt-get update && apt-get -y install maven netcat-openbsd ca-certificates wget
ENV M2_HOME /usr/
ENV PLUGIN_CLASSPATH /opt/webApp/plugin/ProgQuery.jar
EXPOSE 8080/tcp

# Add app user and group

RUN addgroup app
RUN adduser --disabled-password app 
RUN adduser app app

# Install progQuery webApp

COPY . build/
COPY deploy/application-prod.properties build/src/main/resources/application.properties
RUN mvn -Pprod -f build/pom.xml -Dmaven.test.skip=true package
RUN mkdir -p /opt/webApp
RUN mkdir /opt/webApp/uploads
RUN cp build/target/*.jar /opt/webApp/app.jar
RUN rm -r -f build/
COPY deploy/wait-for /opt/webApp/wait-for
RUN dos2unix /opt/webApp/wait-for
RUN chmod +x /opt/webApp/wait-for

# Install the plugin

COPY plugin/ProgQuery.zip /opt/webApp/plugin/ProgQuery.zip
RUN unzip /opt/webApp/plugin/ProgQuery.zip -d /opt/webApp/plugin/
RUN rm -f /opt/webApp/plugin/ProgQuery.zip
RUN mvn install:install-file -DcreateChecksum=true -Dpackaging=jar -Dfile=/opt/webApp/plugin/ProgQuery.jar -DgroupId=es.uniovi.progQuery -DartifactId=progQuery -Dversion=0.0.1-SNAPSHOT -DgeneratePom=true

# Run

RUN chown -R app:app /opt/webApp
USER app:app
WORKDIR /opt/webApp/
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
