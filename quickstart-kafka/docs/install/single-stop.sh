#!/bin/bash

#export BASE_PATH=`cd $(dirname -- $0)/..; pwd`
export BASE_PATH=`cd $(dirname -- $0); pwd`
#export BASE_PATH=`pwd`

echo "BASE_PATH=$BASE_PATH"

#解压Kafka
cd $BASE_PATH
cd kafka_2.13-2.5.0


sh bin/kafka-server-stop.sh

sh bin/zookeeper-server-stop.sh


ps -ef|grep java | grep kafka

