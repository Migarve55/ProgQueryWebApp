FROM openjdk:12-jdk-alpine
MAINTAINER uo257431 uo257431@uniovi.es
VOLUME /tmp

# Install the basics

RUN apk update && apk add maven netcat-openbsd 
RUN apk --no-cache add ca-certificates wget
ENV M2_HOME /usr/
ENV PLUGIN_CLASSPATH /opt/webApp/plugin/ProgQuery.jar
EXPOSE 8080/tcp

# Install java fx

RUN wget --quiet --output-document=/etc/apk/keys/sgerrand.rsa.pub https://alpine-pkgs.sgerrand.com/sgerrand.rsa.pub
RUN wget https://github.com/sgerrand/alpine-pkg-java-openjfx/releases/download/8.151.12-r0/java-openjfx-8.151.12-r0.apk
RUN apk add --no-cache java-openjfx-8.151.12-r0.apk

# Add app user and group

RUN addgroup -S app 
RUN adduser -S app -G app --disabled-password
USER app:app

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

WORKDIR /opt/webApp/
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
