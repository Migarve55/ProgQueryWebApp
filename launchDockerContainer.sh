#!/bin/sh
APP_NAME=proqQueryWebApp
IMG_NAME=pq_web_app

echo "Launching instance"
docker pull $IMG_NAME
docker create --name $APP_NAME --publish 8080:80 $IMG_NAME
docker start $APP_NAME