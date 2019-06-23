#!/bin/sh
APP_NAME=webApp
IMG_NAME=uo257431/prog_query_web_app

echo "Creating container"
docker create --name=$APP_NAME --publish=80:80 $IMG_NAME

echo "Launching container"
docker start $APP_NAME