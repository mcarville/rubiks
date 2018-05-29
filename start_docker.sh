#!/bin/bash

docker run -itd -p 9092:9092 -p 9000:9000 -e KAFKA_ADVERTISED_HOST_NAME=192.168.71.131 mcarville/sandbox:ha_kafka_5

docker run --network="host" -itd  -e DOCKER_ALIAS=robot1 mcarville/sandbox:rubiks-cube-robot

docker run --network="host" -itd  -e DOCKER_ALIAS=robot2 mcarville/sandbox:rubiks-cube-robot

docker run -itd --network="host"  mcarville/sandbox:rubiks.master.002b060