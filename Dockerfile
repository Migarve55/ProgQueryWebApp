FROM openjdk:11
LABEL version="1.0"
LABEL maintainer="uo257431@uniovi.es"
VOLUME /tmp

# Install the basics

RUN apt-get update && apt-get -y install maven netcat-openbsd ca-certificates wget dos2unix
ENV M2_HOME /usr/
ENV PLUGIN_CLASSPATH /opt/webApp/plugin/ProgQuery.jar
ENV PKCS12_KEY xXFFc7j9BRdN8BUy
EXPOSE 8443/tcp

# Add app user and group

RUN adduser --disabled-password app 
RUN adduser app app

# Install Prog Query Cypher Adapter

COPY . build/
COPY lib/ProgQueryCypherAdapter.jar PQCA.jar
RUN mvn install:install-file -DcreateChecksum=true -Dpackaging=jar -Dfile=PQCA.jar -DgroupId=es.uniovi -DartifactId=cypherAdapter -Dversion=1.0 -DgeneratePom=true
RUN rm PQCA.jar

# Install ProgQuery WebApp

RUN keytool -genkey -noprompt -dname "CN=UO257431, OU=uniovi, O=uniovi, L=Oviedo, S=Asturias, C=ES" -storepass $PKCS12_KEY -alias progquerywebapp -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore build/src/main/resources/https_key.p12 -validity 3650
COPY deploy/application-prod.properties build/src/main/resources/application.properties
RUN mvn -Pprod -f build/pom.xml -Dmaven.test.skip=true package
RUN mkdir -p /opt/webApp
RUN mkdir /opt/webApp/uploads
RUN cp build/target/*.jar /opt/webApp/app.jar
RUN rm -rf build/
COPY deploy/wait-for /opt/webApp/wait-for
RUN dos2unix /opt/webApp/wait-for
RUN chmod +x /opt/webApp/wait-for

# Install the plugin

COPY plugin/ProgQuery.zip /opt/webApp/plugin/ProgQuery.zip
RUN unzip /opt/webApp/plugin/ProgQuery.zip -d /opt/webApp/plugin/
RUN rm -f /opt/webApp/plugin/ProgQuery.zip
RUN chown -R app:app /opt/webApp
USER app:app
RUN mvn install:install-file -DcreateChecksum=true -Dpackaging=jar -Dfile=/opt/webApp/plugin/ProgQuery.jar -DgroupId=es.uniovi.progQuery -DartifactId=progQuery -Dversion=0.0.1-SNAPSHOT -DgeneratePom=true

# Run

WORKDIR /opt/webApp/
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
