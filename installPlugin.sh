#!/bin/sh

mvn install:install-file -DcreateChecksum=true -Dpackaging=jar -Dfile=%PLUGIN_CLASSPATH% -DgroupId=es.uniovi.progQuery -DartifactId=progQuery -Dversion=0.0.1-SNAPSHOT -DgeneratePom=true