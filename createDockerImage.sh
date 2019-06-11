#!/bin/sh

IMAGE_NAME=pq_web_app

echo "Building and pushing"
mvn -Dmaven.test.skip=true package
docker build -t $IMAGE_NAME .