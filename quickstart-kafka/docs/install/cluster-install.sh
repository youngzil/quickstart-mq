#!/bin/bash

BASE_DIR=$(dirname $0)
echo $BASE_DIR

KAFKA_DATA=/Users/lengfeng/kafka
KAFKA_HOME=/Users/lengfeng/kafka/kafka_2.13-2.8.0

function installSingleNode()
{
  mkdir -p $KAFKA_DATA/config
  mkdir -p $KAFKA_DATA/data/zookeeper1
  mkdir -p $KAFKA_DATA/data/zookeeper2
  mkdir -p $KAFKA_DATA/data/zookeeper3
  mkdir -p $KAFKA_DATA/data/kafka1
  mkdir -p $KAFKA_DATA/data/kafka2
  mkdir -p $KAFKA_DATA/data/kafka3
  mkdir -p $KAFKA_DATA/logs1
  mkdir -p $KAFKA_DATA/logs2
  mkdir -p $KAFKA_DATA/logs3

  cp config/zookeeper.properties $KAFKA_DATA/config/zookeeper1.properties
  cp config/zookeeper.properties $KAFKA_DATA/config/zookeeper2.properties
  cp config/zookeeper.properties $KAFKA_DATA/config/zookeeper3.properties
  echo "1" > $KAFKA_DATA/data/zookeeper1/myid
  echo "2" > $KAFKA_DATA/data/zookeeper2/myid
  echo "3" > $KAFKA_DATA/data/zookeeper3/myid

  cp config/server.properties $KAFKA_DATA/config/server1.properties
  cp config/server.properties $KAFKA_DATA/config/server2.properties
  cp config/server.properties $KAFKA_DATA/config/server3.properties

  cd $KAFKA_DATA

  # mac下sed命令有''
  sed -i '' "s/2181/2181/g" config/zookeeper1.properties
  sed -i '' "s/2181/2182/g" config/zookeeper2.properties
  sed -i '' "s/2181/2183/g" config/zookeeper3.properties

  sed -i '' "s/zookeeper/zookeeper1/g" config/zookeeper1.properties
  sed -i '' "s/zookeeper/zookeeper2/g" config/zookeeper2.properties
  sed -i '' "s/zookeeper/zookeeper3/g" config/zookeeper3.properties

  sed -i '' "s/logs/logs1/g" config/zookeeper1.properties
  sed -i '' "s/logs/logs2/g" config/zookeeper2.properties
  sed -i '' "s/logs/logs2/g" config/zookeeper3.properties

  # mac下sed命令有''
  sed -i '' "s/9092/9092/g" config/server2.properties
  sed -i '' "s/9092/9093/g" config/server2.properties
  sed -i '' "s/9092/9094/g" config/server3.properties
  sed -i '' "s/broker.id=0/broker.id=1/g" config/server1.properties
  sed -i '' "s/broker.id=0/broker.id=2/g" config/server2.properties
  sed -i '' "s/broker.id=0/broker.id=3/g" config/server3.properties
  sed -i '' "s/data\/kafka/data\/kafka1/g" config/server1.properties
  sed -i '' "s/data\/kafka/data\/kafka2/g" config/server2.properties
  sed -i '' "s/data\/kafka/data\/kafka3/g" config/server3.properties

  tar -xzvf kafka_2.13-2.8.0.tgz

  ## 指定kafka的日志路径
  export LOG_DIR=$KAFKA_DATA/logs
#  export JAVA_HOME=/data/program/java

  nohup $KAFKA_HOME/bin/zookeeper-server-start.sh $KAFKA_DATA/config/zookeeper1.properties > ${LOG_DIR}1/zookeeper-start1.log 2>&1 &
  nohup $KAFKA_HOME/bin/zookeeper-server-start.sh $KAFKA_DATA/config/zookeeper2.properties > ${LOG_DIR}2/zookeeper-start2.log 2>&1 &
  nohup $KAFKA_HOME/bin/zookeeper-server-start.sh $KAFKA_DATA/config/zookeeper3.properties > ${LOG_DIR}3/zookeeper-start3.log 2>&1 &

  sleep 5

  nohup $KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_DATA/config/server1.properties > ${LOG_DIR}1/kafka-start1.log 2>&1 &
  nohup $KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_DATA/config/server2.properties > ${LOG_DIR}2/kafka-start2.log 2>&1 &
  nohup $KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_DATA/config/server3.properties > ${LOG_DIR}3/kafka-start3.log 2>&1 &

}

index=0

function installManyNodes()
{
  cat config/hostlist | while read line
    do
      host=`echo "$line"|awk '{print $1}'`
      username=`echo "$line"|awk '{print $2}'`
      passwd=`echo "$line"|awk '{print $3}'`

      ((index++));

      echo $index $host $username $passwd

      cp config/server_template.properties config/server$index.properties
      cp config/zookeeper.properties config/zookeeper.properties



      cp config/server_template.properties config/server.properties
      # mac下sed命令有''
      sed -i '' "s/127.0.0.1/$host/g" config/server.properties
      sed -i '' "s/broker.id=0/broker.id=$index/g" config/server.properties
  #    sed -i "s/127.0.0.1/$host/g" config/server.properties

      scp config/kafka_2.13-2.8.0.tgz ${username}@$host:/data/program
      scp config/zookeeper.properties ${username}@$host:/data/program
      scp config/server.properties ${username}@$host:/data/program

      scp config/startZookeeper.sh ${username}@$host:/data/program
      scp config/startKafka.sh ${username}@$host:/data/program

      rm -rf config/server.properties

      ssh -n ${username}@$host "cd /data/program;
      tar -xzvf kafka_2.13-2.8.0.tgz;
      mkdir -p /data/program/data/zookeeper;
      mkdir -p /data/program/data/kafka;
      mkdir -p /data/program/logs/zookeeper;
      mkdir -p /data/program/logs/kafka;
      mv zookeeper.properties kafka_2.13-2.8.0/config;
      mv server.properties kafka_2.13-2.8.0/config;
      mv startZookeeper.sh kafka_2.13-2.8.0;
      mv startKafka.sh kafka_2.13-2.8.0;
      echo $index > /data/program/data/zookeeper/myid"

    done

}

source ./cluster-install-kafka.sh
source ./cluster-install-zookeeper.sh

#deploy_kafka
#deploy_zookeeper

installSingleNode

# bin/kafka-server-stop.sh
# bin/zookeeper-server-stop.sh
