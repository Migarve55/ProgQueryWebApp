#!/bin/sh
APP_NAME = proqQueryWebApp
IMG_NAME = uo257431/prog_query_web_app

echo "Stoping current instance"
docker stop $APP_NAME
echo "Launching instance"
docker pull $IMG_NAME
docker create --name $APP_NAME $IMG_NAME
docker start --attach $APP_NAME