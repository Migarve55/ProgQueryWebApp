#!/bin/sh

IMAGE_NAME=pqWebApp

echo "Building and pushing"
mvn -Dmaven.test.skip=true package
docker build -t $IMAGE_NAME .