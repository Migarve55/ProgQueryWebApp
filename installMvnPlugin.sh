#!/bin/sh

cd src/main/resources/
mvn install:install-file -DcreateChecksum=true -Dpackaging=jar -Dfile=plugin/ProgQuery.jar -DgroupId=es.uniovi.progQuery -DartifactId=progQuery -Dversion=0.0.1-SNAPSHOT -DgeneratePom=true