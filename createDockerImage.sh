#!/bin/sh

DOCKER_REPO=uo257431/prog_query_web_app

echo "Login..."
docker login --username=uo257431 --email=uo257431@uniovi.es

echo "Building and pushing"
mvn -Dmaven.test.skip=true package
docker build -t $DOCKER_REPO
docker push $DOCKER_REPO