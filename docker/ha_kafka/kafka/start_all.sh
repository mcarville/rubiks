#!/bin/bash

KAFKA_HOME="/opt/kafka"

cd $KAFKA_HOME

if [ ! -z "$KAFKA_ADVERTISED_HOST_NAME" ]; then
	OVERRIDE="--override advertised.listeners=PLAINTEXT://$KAFKA_ADVERTISED_HOST_NAME:9092"
fi

bin/zookeeper-server-start.sh -daemon config/zookeeper.properties

# wait for zookeeper to start
PIDS=""

while [ -z "$PIDS" ]
do
	echo "Waiting for ZK to start"
	sleep 1s
	PIDS=$(ps ax | grep java | grep -i QuorumPeerMain | grep -v grep | awk '{print $1}')
done

bin/kafka-server-start.sh -daemon config/server.properties $OVERRIDE

# re-create topics

# wait for kafka to start
PIDS=""

while [ -z "$PIDS" ]
do
	echo "Waiting for Kafka to start"
	sleep 1s
	PIDS=$(ps ax | grep -i 'kafka\.Kafka' | grep java | grep -v grep | awk '{print $1}')
done

for var in "sandbox" "request" "response"
do
  echo "${var}"
	bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic $var
done

java -jar $KAFKA_HOME/Kafdrop/target/kafdrop-2.0.6.jar --zookeeper.connect="127.0.0.1:2181" &

sleep infinity
