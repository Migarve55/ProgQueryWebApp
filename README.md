
 # ProgQuery Web Application
 
 ## About
 
 This a front end web application for the java compiler plugin [ProgQuery](https://github.com/OscarRodriguezPrieto/ProgQuery).
 
 ## Technologies
 
 - Spring Boot 2.1.4
 - Thymeleaf 3.0.3
 - MySQL 8.0
 - Docker 18.09
 - Docker-compose 3
 - Neo4j 3.5.4
 - Bootstrap 4.3.1
 - jQuery 3.2.0
 
 ## Features
 
 - Java code analysys and problem detection
 - Query creating and sharing
 - Persistent program analysis storage: analyze your programs again and again without needing to upload them
 - Both in English and in Spanish
 
 ## Instalation
 
 This is prepared to run in a linux based docker container.
 This is the docker hub [repo](https://hub.docker.com/r/uo257431/prog_query_web_app).
 
 Just use `sudo docker-compose up` to launch it quickly.
 
 Three enviroment variables need to be set:
 
 - M2_HOME: to the maven folder
 - PLUGIN_CLASSPATH: path the plugin path
 - SHOW_COMPILE_OUTPUT: this is optional, if set to 'yes' all the compilers will show their output, only for debbuging purposes
 
 The first two are already set in the docker image, so you do not have to worry about them.
 
 ## Usage
 
 Follow this steps to analize a program:
 
 1. Pick an analyzer option
 2. Select the queries
 3. Click "Analyze"
 4. Wait... 
 5. Done!!! Enjoy your report
 
 Or...
 
 1. Go to "programs"
 2  Pick a program that you want to analyze
 3. Select the queries
 4. Click "Analize"
 5. Wait...
 6. Done!!! Enjoy your report
 
 ## Credit
 
 - [Francisco Ortin](https://github.com/francisco-ortin) final project director.
 - [Óscar Rodríguez Prieto](https://github.com/OscarRodriguezPrieto) the creator of the ProgQuery plugin.
 - [Miguel Garcia Rodriguez](https://github.com/miguelgrdotcom) helped a lot with the deployment.
 
