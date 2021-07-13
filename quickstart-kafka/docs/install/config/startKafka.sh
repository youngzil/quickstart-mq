#!/bin/bash

#BASE_DIR=/data/program/kafka
#KAFKA_DIR=$BASE_DIR/kafka_2.13-2.8.0
BASE_DIR=/data/program/kafka2
KAFKA_DIR=$BASE_DIR/kafka_2.12-1.0.0
echo $BASE_DIR

## 指定kafka的日志路径
export LOG_DIR=$BASE_DIR/logs
export JAVA_HOME=/data/program/java

nohup $KAFKA_DIR/bin/kafka-server-start.sh $KAFKA_DIR/config/server.properties > $LOG_DIR/kafka-start.log 2>&1 &
