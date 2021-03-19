
[![Docker build](https://img.shields.io/docker/cloud/build/uo257431/prog_query_web_app.svg)](https://cloud.docker.com/u/uo257431/repository/docker/uo257431/prog_query_web_app)
[![Docker pulls](https://img.shields.io/docker/pulls/uo257431/prog_query_web_app.svg)](https://cloud.docker.com/u/uo257431/repository/docker/uo257431/prog_query_web_app)
[![Docker layers](https://img.shields.io/microbadger/layers/uo257431/prog_query_web_app.svg)](https://cloud.docker.com/u/uo257431/repository/docker/uo257431/prog_query_web_app)

 # ProgQuery Web Application
 
 ## About
 
 This a front end web application for the java compiler plugin [ProgQuery](https://github.com/OscarRodriguezPrieto/ProgQuery).
 
 ## Technologies
 
 - Spring Boot 2.1.4
 - Thymeleaf 3.0.3
 - MySQL 8.0
 - Docker 18.09
 - Docker-compose 3
 - Neo4j 4.2.3
 - Bootstrap 4.3.1
 - jQuery 3.2.0
 
 ## Features
 
 - Java code analyses and problem detection
 - Query creating and sharing
 - Persistent program analysis storage: analyze your programs again and again without needing to upload them
 - Both in English and in Spanish
 
 ## Instalation
 
 This is prepared to run in a linux based docker container.
 This is the docker hub [repo](https://hub.docker.com/r/uo257431/prog_query_web_app).
 
 Just use `sudo docker-compose up` to launch it quickly.
 
 Three enviroment variables need to be set:
 
 - M2_HOME: to the maven folder
 - PLUGIN_CLASSPATH: path to the plugin 
 - MYSQL_DB_PASSWORD: password of the sql database
 - NEO4J_DB_PASSWORD: password of the neo4j database
 - HIDE_COMPILER_OUTPUT: this is optional, if set all the compilers will hide their output
 - SHOW_DEBUG_OUTPUT: this is optional, if set all the compilers will show the debug info
 
 The first two are already set in the docker image, so you do not have to worry about them.
 
 ## Credit
 
 - [Francisco Ortin](https://github.com/francisco-ortin) final project director.
 - [Óscar Rodríguez Prieto](https://github.com/OscarRodriguezPrieto) the creator of the ProgQuery plugin.
 - [Miguel Garcia Rodriguez](https://github.com/miguelgrdotcom) helped a lot with the deployment.
 
