#!/bin/sh
APP_NAME=proqQueryWebApp
IMG_NAME=pqWebApp

echo "Stoping current instance"
docker stop $APP_NAME
echo "Launching instance"
docker pull $IMG_NAME
docker create --name $APP_NAME --publish 8080:80 $IMG_NAME
docker start $APP_NAME